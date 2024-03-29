package rpi;

import ax.dkarlsso.hottub.controller.rpi.configurator.CirculationPumpConfigurator;
import ax.dkarlsso.hottub.controller.rpi.configurator.HeaterConfigurator;
import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import dkarlsso.commons.raspberry.exception.NoConnectionException;
import dkarlsso.commons.raspberry.relay.StubRelay;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import ax.dkarlsso.hottub.controller.rpi.Heater;
import dkarlsso.commons.raspberry.sensor.temperature.TemperatureSensor;
import dkarlsso.commons.repository.settings.SettingsFilesystemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Dont look at this code, it is older and uglier than time itself
 */
public class HeaterTest {
    private final Settings settings = new Settings();
    private OperationsService operationsService;

    private final TemperatureSensor returnTempMax = Mockito.mock(TemperatureSensor.class);
    private final TemperatureSensor overTempMax = Mockito.mock(TemperatureSensor.class);
    private Heater heater;

    private final RelayInterface relay = new StubRelay();

    @BeforeEach
    public void setup() {
        final SettingsFilesystemRepository<Settings> settingsFilesystemRepository = Mockito.mock(SettingsFilesystemRepository.class);
        when(settingsFilesystemRepository.read()).thenReturn(settings);
        operationsService = new OperationsService(settingsFilesystemRepository);

        settings.setDebug(true);
        operationsService.updateSettings(settings);
        settings.setTemperatureDiff(0);
        heater = new Heater(List.of(new HeaterConfigurator(new StubRelay()), new CirculationPumpConfigurator(new StubRelay())),
                operationsService,overTempMax,returnTempMax,relay,relay);
    }

    @Test
    public void loop_givenLowReturnTemp_expectHeaterTurnedOn() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(25.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(40.0);

        settings.setHottubTemperatureLimit(37);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        System.out.println(operationalData.getHottubTemperature());

        assertTrue(operationalData.isCirculating()); // Always tru while heating with the weaker circulation pump
        assertTrue(operationalData.isHeating());
    }

    @Test
    public void loop_givenHighReturnTemp_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(15.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(15.0);

        settings.setHottubTemperatureLimit(12);
        settings.setHeaterTemperatureLimit(20);
        operationsService.updateSettings(settings);

        heater.loop();
        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void loop_givenNoSignalFromHeaterSensor_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(37.0);
        Mockito.when(overTempMax.readTemp()).thenThrow(new NoConnectionException());

        settings.setHottubTemperatureLimit(35);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void loop_givenHighTemperatureInHeater_expectCirculationPumpOn() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(36.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(58.0);

        settings.setHottubTemperatureLimit(37);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());
    }

    @Test
    public void loop_givenNoSignalFromHotTubSensor_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenThrow(new NoConnectionException());
        Mockito.when(overTempMax.readTemp()).thenReturn(59.0);

        settings.setHottubTemperatureLimit(35);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void loop_whenHeaterTemperatureAreOverLimit_expectCirculationPumpTurnedOn() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(30.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(55.0);

        settings.setHottubTemperatureLimit(37);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());
    }

    /**
     * Tests that if wanted temperature is 37, and heater reaches it, that it does not start until temperature drops over 1 degree
     * This is to avoid start and stops every 15 seconds
     */
    @Test
    public void loop_givenDeltaTemperatureAndHeaterReachingTemperature_expectDropInTemperatureBeforeHeatingStartsAgain() throws Exception {
        when(returnTempMax.readTemp()).thenReturn(35.0);
        when(overTempMax.readTemp()).thenReturn(42.0);

        settings.setHottubTemperatureLimit(37);
        settings.setHeaterTemperatureLimit(42);
        operationsService.updateSettings(settings);

        heater.loop();

        OperationalData operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(43.0);
        heater.loop();

        operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(37.0);
        when(overTempMax.readTemp()).thenReturn(44.0);
        heater.loop();

        operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(42.0);
        heater.loop();
        operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(35.0);
        heater.loop();
        assertTrue(operationsService.getOperationalData().isHeating());
    }


    @Test
    public void loop_givenCriticallyHighValues_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(37.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(45.0);

        settings.setHottubTemperatureLimit(8);
        settings.setHeaterTemperatureLimit(25);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void loop_givenAbnormalReturnTemp_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(25.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(25.0);

        settings.setHottubTemperatureLimit(8);
        settings.setHeaterTemperatureLimit(27);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }


    @Test // Temperature diff is the fact that the top of the water might be 2 - 3 degrees higher than the bottom where the sensor is
    public void loop_withHighTemperatureDiff_assertItIsTakenIntoConsideration() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(35.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(44.0);

        // 35 + 3 = 38 == should not heat
        settings.setTemperatureDiff(3);
        settings.setHottubTemperatureLimit(37);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(38, operationalData.getHottubTemperature());
        assertFalse(operationalData.isHeating());
    }

    @Test // Temperature diff is the fact that the top of the water might be 2 - 3 degrees higher than the bottom where the sensor is
    public void loop_withHighTemperatureDiff_assertHeaterisHeatingWhenBelowTemperature() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(33.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(44.0);

        // 33 + 3 = 36 == should heat
        settings.setTemperatureDiff(3);
        settings.setHottubTemperatureLimit(37);
        settings.setHeaterTemperatureLimit(45);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(36, operationalData.getHottubTemperature());
        assertTrue(operationalData.isHeating());
    }
}