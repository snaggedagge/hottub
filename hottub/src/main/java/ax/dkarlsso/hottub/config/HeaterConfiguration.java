package ax.dkarlsso.hottub.config;

import ax.dkarlsso.hottub.controller.rpi.Heater;
import ax.dkarlsso.hottub.controller.rpi.HeaterInterface;
import ax.dkarlsso.hottub.controller.rpi.HeaterThread;
import ax.dkarlsso.hottub.controller.rpi.configurator.OperationsConfigurator;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HeaterConfiguration {

    @Bean("heatingRelay")
    public RelayInterface heatingRelay() {
        if(OSHelper.isRaspberryPi()) {
            return new OptoRelay(GPIOPins.GPIO13);
        }
        else {
            return new StubRelay();
        }
    }

    @Bean("circulationRelay")
    public RelayInterface circulationRelay() {
        if(OSHelper.isRaspberryPi()) {
            return new OptoRelay(GPIOPins.GPIO26);
        }
        else {
            return new StubRelay();
        }
    }

    @Bean("lightRelay")
    public RelayInterface lightRelay() {
        if(OSHelper.isRaspberryPi()) {
            return new OptoRelay(GPIOPins.GPIO19);
        }
        else {
            return new StubRelay();
        }
    }

    @Bean
    public HeaterInterface heaterInterface(@Autowired final OperationsService operationsService,
                                           @Autowired final List<OperationsConfigurator> operationsConfigurators,
                                           @Qualifier("circulationRelay") final RelayInterface circulationRelay,
                                           @Qualifier("heatingRelay") final RelayInterface heatingRelay) {
        final HeaterInterface heater;
        if(OSHelper.isRaspberryPi()) {
            final TemperatureSensor returnTemperatureSensor = new DS18B20("28-030c979428d4");
            final TemperatureSensor heatingPanSensor = new DS18B20("28-030297942385");
            heater = new Heater(operationsConfigurators, operationsService, heatingPanSensor, returnTemperatureSensor,
                    heatingRelay, circulationRelay);
            GpioFactory.getInstance();
        }
        else {
            final TemperatureSensor returnTempSensor = () -> 20.2;
            final TemperatureSensor overTempSensor = () -> 40.2;
            heater = new Heater(operationsConfigurators, operationsService, overTempSensor, returnTempSensor,
                    heatingRelay, circulationRelay);
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
