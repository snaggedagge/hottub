package ax.dkarlsso.hottub.config;

import ax.dkarlsso.hottub.controller.rpi.Heater;
import ax.dkarlsso.hottub.controller.rpi.HeaterInterface;
import ax.dkarlsso.hottub.controller.rpi.HeaterThread;
import ax.dkarlsso.hottub.service.OperationsService;
import ax.dkarlsso.hottub.service.RunningTimeService;
import com.pi4j.io.gpio.GpioFactory;
import dkarlsso.commons.raspberry.OSHelper;
import dkarlsso.commons.raspberry.enums.GPIOPins;
import dkarlsso.commons.raspberry.relay.OptoRelay;
import dkarlsso.commons.raspberry.relay.StubRelay;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.DS18B20;
import dkarlsso.commons.raspberry.sensor.temperature.TemperatureSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeaterConfiguration {

    @Bean
    public HeaterInterface heaterInterface(@Autowired final OperationsService operationsService) {
        final HeaterInterface heater;
        if(OSHelper.isRaspberryPi()) {
            final TemperatureSensor returnTemperatureSensor = new DS18B20("28-030c979428d4");
            final TemperatureSensor heatingPanSensor = new DS18B20("28-030297942385");

            final RelayInterface lightRelay = new OptoRelay(GPIOPins.GPIO19);
            final RelayInterface heatingRelay = new OptoRelay(GPIOPins.GPIO13);
            final RelayInterface circulationRelay = new OptoRelay(GPIOPins.GPIO26);

            heater = new Heater(operationsService, heatingPanSensor, returnTemperatureSensor,
                    heatingRelay, circulationRelay, lightRelay);
            GpioFactory.getInstance();
        }
        else {
            final TemperatureSensor returnTempSensor = () -> 20.2;
            final TemperatureSensor overTempSensor = () -> 40.2;
            RelayInterface mockRelay = new StubRelay();
            heater = new Heater(operationsService, overTempSensor, returnTempSensor,
                    mockRelay, mockRelay, mockRelay);
        }
        return heater;
    }

    @Bean
    public HeaterThread heaterThread(@Autowired final OperationsService operationsService,
                                     @Autowired final RunningTimeService runningTimeService,
                                     @Autowired final HeaterInterface heaterInterface) {
        final HeaterThread thread;
        thread = new HeaterThread(operationsService, runningTimeService, heaterInterface);
        thread.start();
        return thread;
    }
}
