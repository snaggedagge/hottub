package rpi;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import dkarlsso.commons.raspberry.exception.NoConnectionException;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.MAX6675;
import hottub.controller.rpi.Heater;
import hottub.model.settings.HeaterDataSettings;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("Duplicates")
@RunWith(EasyMockRunner.class)
public class HeaterTest {
    private HeaterDataSettings heaterDTO = new HeaterDataSettings();

    @Mock
    RelayInterface relay;

    @Test
    public void testLowTemp() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(25.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(40.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(37);
        heaterDTO.setOverTempLimit(45);

        heater.loop();
        System.out.println(heaterDTO.getReturnTemp());

        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), true);
    }

    @Test
    public void testHighTemp() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(15.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(15.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(12);
        heaterDTO.setOverTempLimit(20);

        heater.loop();
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), false);
    }

    @Test
    public void testHighOverTemp() throws Exception {

        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(34.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(59.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(37);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), true);
        assertEquals(heaterDTO.isHeating(), true);
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
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(35);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), false);
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
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(35);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), false);
    }

    @Test
    public void testHighOverTemp2() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(30.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(80.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(35);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), true);
        assertEquals(heaterDTO.isHeating(), false);
    }

    @Test
    public void testHeatingCirculating() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(30.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(55.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(37);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), true);
        assertEquals(heaterDTO.isHeating(), true);
    }

    @Test
    public void testDeltaTemp() throws Exception {
        MAX6675 returnTempMax = mock(MAX6675.class);
        MAX6675 overTempMax = mock(MAX6675.class);

        when(returnTempMax.readTemp()).thenReturn(35.0);
        when(overTempMax.readTemp()).thenReturn(42.0);

        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);
        heaterDTO.setReturnTempLimit(37);
        heaterDTO.setOverTempLimit(42);
        heater.loop();
        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), true);

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(43.0);
        heater.loop();
        assertEquals(heaterDTO.isCirculating(), true);
        assertEquals(heaterDTO.isHeating(), true);

        when(returnTempMax.readTemp()).thenReturn(37.0);
        when(overTempMax.readTemp()).thenReturn(44.0);
        heater.loop();
        assertEquals(heaterDTO.isCirculating(), true);
        assertEquals(heaterDTO.isHeating(), false);

        when(returnTempMax.readTemp()).thenReturn(36.0);
        when(overTempMax.readTemp()).thenReturn(42.0);
        heater.loop();
        assertEquals(heaterDTO.isCirculating(), true);
        assertEquals(heaterDTO.isHeating(), false);
    }


    @Test
    public void testReset() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(37.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(45.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(8);
        heaterDTO.setOverTempLimit(25);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), false);
    }

    @Test
    public void testReset2() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(25.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(25.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        heaterDTO.setReturnTempLimit(8);
        heaterDTO.setOverTempLimit(23);
        heater.loop();

        assertEquals(heaterDTO.isCirculating(), false);
        assertEquals(heaterDTO.isHeating(), false);
    }


    @Test
    public void testTemperatureDiffNotHeating() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(35.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(44.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        // 35 + 3 = 38 == should not heat
        heaterDTO.setTemperatureDiff(3);
        heaterDTO.setReturnTempLimit(37);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(38, heaterDTO.getReturnTemp());
        assertEquals(heaterDTO.isHeating(), false);
    }

    @Test
    public void testTemperatureDiffHeating() throws Exception {
        MAX6675 returnTempMax = EasyMock.mock(MAX6675.class);
        MAX6675 overTempMax = EasyMock.mock(MAX6675.class);
        EasyMock.expect(returnTempMax.readTemp()).andStubReturn(33.0);
        EasyMock.expect(overTempMax.readTemp()).andStubReturn(44.0);
        EasyMock.replay(overTempMax);
        EasyMock.replay(returnTempMax);
        Heater heater = new Heater(overTempMax,returnTempMax,heaterDTO,relay,relay,relay);

        // 33 + 3 = 36 == should heat
        heaterDTO.setTemperatureDiff(3);
        heaterDTO.setReturnTempLimit(37);
        heaterDTO.setOverTempLimit(45);
        heater.loop();

        assertEquals(36, heaterDTO.getReturnTemp());
        assertEquals(heaterDTO.isHeating(), true);
    }
}