package ax.dkarlsso.hottub.controller.rpi.configurator;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.utils.TimeElapsedTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Configures when the circulation pump should activate
 */
@Component
@Order(2)
public class CirculationPumpConfigurator implements OperationsConfigurator {
    private final static Logger log = LoggerFactory.getLogger(HeaterConfigurator.class);

    /** Parameter to avoid fast on/off/on behavior, letting relays rest when set point has been reached */
    private int circulationTemperatureDelta = 0;

    private final TimeElapsedTimer timer = new TimeElapsedTimer();


    @Override
    public void configure(final OperationalData operationalData,
                          final Settings settings) {
        if(operationalData.getOverTemp() + circulationTemperatureDelta > settings.getOverTempLimit())
        {
            circulationTemperatureDelta = 3;
            operationalData.setCirculating(true);
        } else {
            circulationTemperatureDelta = 0;
            operationalData.setCirculating(false);
        }
        operationalData.setCirculateBasedOnTimer(this.shouldCirculateOnTimer(settings));
        operationalData.setCirculating(operationalData.isCirculating() || operationalData.isCirculateBasedOnTimer());
    }

    private boolean shouldCirculateOnTimer(final Settings settings) {
        if(timer.hasTimePassed(settings.getCirculationTimeCycle()))
        {
            if(timer.hasTimePassed(settings.getCirculationTimeCycle().plusSeconds(30))) {
                timer.reset();
            }
            return true;
        }
        return false;
    }
}
