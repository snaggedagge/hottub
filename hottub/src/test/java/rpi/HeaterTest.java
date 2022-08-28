package rpi;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import dkarlsso.commons.raspberry.exception.NoConnectionException;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.MAX6675;
import ax.dkarlsso.hottub.controller.rpi.Heater;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("Duplicates")
@RunWith(EasyMockRunner.class)
public class HeaterTest {
    private final Settings settings = new Settings();
    private final OperationsService operationsService = new OperationsService(settings);

    @Mock
    RelayInterface relay;

    @Before
    public void setup() {
        settings.setDebug(true);
        operationsService.updateSettings(settings);
        settings.setTemperatureDiff(0);
    }

    @Test
    public void testLowTemp() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(25.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(40.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        System.out.println(operationalData.getReturnTemp());

        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), true);
    }

    @Test
    public void testHighTemp() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(15.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(15.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(12);
        settings.setOverTempLimit(20);
        operationsService.updateSettings(settings);

        heater.loop();
        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), false);
    }

    @Test
    public void testHighOverTemp() throws Exception {

        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(36.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(58.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(true, operationalData.isCirculating());
        assertEquals(true, operationalData.isHeating());
    }

    //@Ignore
    @Test
    public void testOverTempNoSignal() throws Exception {

        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(37.0);
        EasyMock.expect(overTempMax.readTemp()).andThrow(new NoConnectionException());
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(35);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), false);
    }

    //@Ignore
    @Test
    public void testRetTempNoSignal() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andThrow(new NoConnectionException());
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(59.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(35);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), false);
    }

    @Test
    public void testHighOverTemp2() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(30.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(80.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(35);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), true);
        assertEquals(operationalData.isHeating(), false);
    }

    @Test
    public void testHeatingCirculating() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(30.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(55.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);

        heater.loop();
        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), true);
        assertEquals(operationalData.isHeating(), true);
    }

    @Test
    public void testDeltaTemp() throws Exception {
        MAX6675 returnTempMax = mock(MAX6675.class);
        MAX6675 overTempMax = mock(MAX6675.class);

        when(returnTempMax.readTemp()).thenReturn(35.0);
        when(overTempMax.readTemp()).thenReturn(42.0);

        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);
        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(42);
        operationsService.updateSettings(settings);

        heater.loop();

        OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), true);

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(43.0);
        heater.loop();

        operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), true);
        assertEquals(operationalData.isHeating(), true);

        when(returnTempMax.readTemp()).thenReturn(37.0);
        when(overTempMax.readTemp()).thenReturn(44.0);
        heater.loop();

        operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), true);
        assertEquals(operationalData.isHeating(), false);

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(42.0);
        heater.loop();
        operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), true);
        assertEquals(operationalData.isHeating(), false);
    }


    @Test
    public void testReset() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(37.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(45.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(8);
        settings.setOverTempLimit(25);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), false);
    }

    @Test
    public void testReset2() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(25.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(25.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        settings.setReturnTempLimit(8);
        settings.setOverTempLimit(23);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(operationalData.isCirculating(), false);
        assertEquals(operationalData.isHeating(), false);
    }


    @Test
    public void testTemperatureDiffNotHeating() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(35.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(44.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        // 35 + 3 = 38 == should not heat
        settings.setTemperatureDiff(3);
        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(38, operationalData.getReturnTemp());
        assertEquals(operationalData.isHeating(), false);
    }

    @Test
    public void testTemperatureDiffHeating() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(33.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(44.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,operationsService,relay,relay,relay);

        // 33 + 3 = 36 == should heat
        settings.setTemperatureDiff(3);
        settings.setReturnTempLimit(37);
        settings.setOverTempLimit(45);
        operationsService.updateSettings(settings);
        heater.loop();

        final OperationalData operationalData = operationsService.getOperationalData();
        assertEquals(36, operationalData.getReturnTemp());
        assertEquals(operationalData.isHeating(), true);
    }
}