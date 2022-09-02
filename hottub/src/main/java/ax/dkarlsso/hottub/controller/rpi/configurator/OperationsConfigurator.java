package ax.dkarlsso.hottub.controller.rpi.configurator;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;

/**
 * Configurators that will change operations of the hottub,
 * Such as turning off heater etc.
 */
public interface OperationsConfigurator {

    void configure(final OperationalData operationalData, final Settings settings);
}
