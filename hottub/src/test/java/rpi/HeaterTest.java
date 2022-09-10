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
    private final OperationsService operationsService = new OperationsService(settings);

    private final TemperatureSensor returnTempMax = Mockito.mock(TemperatureSensor.class);
    private final TemperatureSensor overTempMax = Mockito.mock(TemperatureSensor.class);
    private Heater heater;

    private final RelayInterface relay = new StubRelay();

    @BeforeEach
    public void setup() {
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

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        System.out.println(operationalData.getReturnTemp());

        assertFalse(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());
    }

    @Test
    public void loop_givenHighReturnTemp_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(15.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(15.0);

        settings.setReturnTempLimit(12);
        settings.setOverTempLimit(20);
        operationsService.updateSettings(settings);

        heater.loop();
        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void testHighOverTemp() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(36.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(58.0);

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());
    }

    //@Ignore
    @Test
    public void testOverTempNoSignal() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(37.0);
        Mockito.when(overTempMax.readTemp()).thenThrow(new NoConnectionException());

        settings.setReturnTempLimit(35);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    //@Ignore
    @Test
    public void testRetTempNoSignal() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenThrow(new NoConnectionException());
        Mockito.when(overTempMax.readTemp()).thenReturn(59.0);

        settings.setReturnTempLimit(35);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void loop_givenCriticallyHighCirculationPumpValues_expectPumpTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(30.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(80.0);

        settings.setReturnTempLimit(35);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void testHeatingCirculating() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(30.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(55.0);

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());
    }

    @Test
    public void testDeltaTemp() throws Exception {
        when(returnTempMax.readTemp()).thenReturn(35.0);
        when(overTempMax.readTemp()).thenReturn(42.0);

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(42);
        operationsService.updateSettings(settings);

        heater.loop();

        OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(43.0);
        heater.loop();

        operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertTrue(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(37.0);
        when(overTempMax.readTemp()).thenReturn(44.0);
        heater.loop();

        operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(42.0);
        heater.loop();
        operationalData = operationsService.getOperationalData();
        assertTrue(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }


    @Test
    public void loop_givenCriticallyHighValues_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(37.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(45.0);

        settings.setReturnTempLimit(8);
        settings.setOverTempLimit(25);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void loop_givenAbnormalReturnTemp_expectHeaterTurnedOff() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(25.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(25.0);

        settings.setReturnTempLimit(8);
        settings.setOverTempLimit(27);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertFalse(operationalData.isCirculating());
        assertFalse(operationalData.isHeating());
    }


    @Test
    public void testTemperatureDiffNotHeating() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(35.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(44.0);

        // 35 + 3 = 38 == should not heat
        settings.setTemperatureDiff(3);
        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(38, operationalData.getReturnTemp());
        assertFalse(operationalData.isHeating());
    }

    @Test
    public void testTemperatureDiffHeating() throws Exception {
        Mockito.when(returnTempMax.readTemp()).thenReturn(33.0);
        Mockito.when(overTempMax.readTemp()).thenReturn(44.0);

        // 33 + 3 = 36 == should heat
        settings.setTemperatureDiff(3);
        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(36, operationalData.getReturnTemp());
        assertTrue(operationalData.isHeating());
    }
}