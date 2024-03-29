package ax.dkarlsso.hottub.controller.rpi.configurator;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Configures when the heater should activate
 */
@Component
@Order(1)
@Slf4j
public class HeaterConfigurator implements OperationsConfigurator {

    /** Parameter to avoid fast on/off/on behavior, letting relays rest when set point has been reached */
    private int heatingTemperatureDelta = 0;

    private final RelayInterface heatingRelay;

    public HeaterConfigurator(@Qualifier("heatingRelay") final RelayInterface heatingRelay) {
        this.heatingRelay = heatingRelay;
    }

    @Override
    public void configure(final OperationalData operationalData,
                          final Settings settings) {
        if(operationalData.getHottubTemperature() + heatingTemperatureDelta < settings.getHottubTemperatureLimit())
        {
            heatingTemperatureDelta = 0;
            operationalData.setHeating(true);
        }
        else {
            heatingTemperatureDelta = 1;
            operationalData.setHeating(false);
        }

        // Sanity checks to be safe
        if (operationalData.getHottubTemperature() > settings.getHottubTemperatureLimit() + 10
                || operationalData.getHeaterTemperature() > settings.getHeaterTemperatureLimit() + 20) {
            log.warn("Turning off due to much higher temperatures");
            operationalData.setHeating(false);
        }

        heatingRelay.setState(operationalData.isHeating());
        if(settings.isDebug()) {
            log.debug("Heating {}", operationalData.isHeating());
        }
    }
}
