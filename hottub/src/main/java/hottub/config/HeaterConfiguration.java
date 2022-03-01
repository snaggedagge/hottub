package hottub.config;

import com.pi4j.io.gpio.GpioFactory;
import dkarlsso.commons.raspberry.OSHelper;
import dkarlsso.commons.raspberry.relay.StubRelay;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.TemperatureSensor;
import hottub.controller.rpi.Heater;
import hottub.controller.rpi.HeaterInterface;
import hottub.controller.rpi.HeaterThread;
import hottub.model.settings.HeaterDataSettings;
import hottub.service.RunningTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeaterConfiguration {

    @Bean
    public HeaterInterface heaterInterface(@Autowired final HeaterDataSettings heaterDTO) {
        final HeaterInterface heater;
        if(OSHelper.isRaspberryPi()) {
            heater = new Heater(heaterDTO);
            GpioFactory.getInstance();
        }
        else {
            final TemperatureSensor returnTempSensor = () -> 20.2;
            final TemperatureSensor overTempSensor = () -> 40.2;
            RelayInterface mockRelay = new StubRelay();
            heater = new Heater(overTempSensor, returnTempSensor, heaterDTO,
                    mockRelay, mockRelay, mockRelay);
        }
        return heater;
    }

    @Bean
    public HeaterThread heaterThread(@Autowired final HeaterDataSettings heaterDTO,
                                     @Autowired final RunningTimeService runningTimeService,
                                     @Autowired final HeaterInterface heaterInterface) {
        final HeaterThread thread;
        thread = new HeaterThread(heaterDTO, runningTimeService, heaterInterface);
        thread.start();
        return thread;
    }
}
