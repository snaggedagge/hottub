package ax.dkarlsso.hottub.config;

import ax.dkarlsso.hottub.controller.rpi.Heater;
import ax.dkarlsso.hottub.controller.rpi.HeaterInterface;
import ax.dkarlsso.hottub.controller.rpi.HeaterThread;
import ax.dkarlsso.hottub.controller.rpi.configurator.OperationsConfigurator;
import ax.dkarlsso.hottub.service.OperationsService;
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
            GpioFactory.getInstance();
            final TemperatureSensor hottubTemperatureSensor = new DS18B20("28-030c979428d4");
            final TemperatureSensor heaterTemperatureSensor = new DS18B20("28-030297942385");
            heater = new Heater(operationsConfigurators, operationsService, heaterTemperatureSensor, hottubTemperatureSensor,
                    heatingRelay, circulationRelay);
        }
        else {
            final TemperatureSensor hottubTemperatureSensor = () -> 20.2;
            final TemperatureSensor heaterTemperatureSensor = () -> 40.2;
            heater = new Heater(operationsConfigurators, operationsService, heaterTemperatureSensor, hottubTemperatureSensor,
                    heatingRelay, circulationRelay);
        }
        return heater;
    }

    @Bean
    public HeaterThread heaterThread(@Autowired final OperationsService operationsService,
                                     @Autowired final HeaterInterface heaterInterface) {
        final HeaterThread thread;
        thread = new HeaterThread(operationsService, heaterInterface);
        thread.start();
        return thread;
    }
}
