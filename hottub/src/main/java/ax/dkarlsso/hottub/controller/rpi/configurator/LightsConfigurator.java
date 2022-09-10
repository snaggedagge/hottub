package ax.dkarlsso.hottub.controller.rpi.configurator;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Configures how lights should work
 */
@Component
@Order(2)
public class LightsConfigurator implements OperationsConfigurator {
    private final RelayInterface lightRelay;

    public LightsConfigurator(@Qualifier("lightRelay") final RelayInterface lightRelay) {
        this.lightRelay = lightRelay;
    }
    @Override
    public void configure(final OperationalData operationalData,
                          final Settings settings) {
        lightRelay.setState(settings.isLightsOn());
    }
}
