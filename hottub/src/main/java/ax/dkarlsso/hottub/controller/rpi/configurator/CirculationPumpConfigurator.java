package ax.dkarlsso.hottub.controller.rpi.configurator;

import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.utils.TimeElapsedTimer;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Configures when the circulation pump should activate
 */
@Component
@Order(2)
@Slf4j
public class CirculationPumpConfigurator implements OperationsConfigurator {

    /** Parameter to avoid fast on/off/on behavior, letting relays rest when set point has been reached */
    private int circulationTemperatureDelta = 0;

    private final TimeElapsedTimer timer = new TimeElapsedTimer();

    private final RelayInterface circulationRelay;

    public CirculationPumpConfigurator(@Qualifier("circulationRelay") final RelayInterface circulationRelay) {
        this.circulationRelay = circulationRelay;
    }

    @Override
    public void configure(final OperationalData operationalData,
                          final Settings settings) {
        // With new circulating pump, always circulate when heating
        operationalData.setCirculating(operationalData.isHeating());

        boolean circulateBasedOnTimer = this.shouldCirculateOnTimer(settings);
        operationalData.setCirculating(operationalData.isCirculating() || circulateBasedOnTimer);

        circulationRelay.setState(operationalData.isCirculating());
        if(settings.isDebug()) {
            log.debug("Circulating {}", operationalData.isCirculating());
            log.debug("Circulating based on timer {}", circulateBasedOnTimer);
        }
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

    /*
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
        boolean circulateBasedOnTimer = this.shouldCirculateOnTimer(settings);
        operationalData.setCirculating(operationalData.isCirculating() || circulateBasedOnTimer);

        circulationRelay.setState(operationalData.isCirculating());
        if(settings.isDebug()) {
            log.debug("Circulating {}", operationalData.isCirculating());
            log.debug("Circulating based on timer {}", circulateBasedOnTimer);
        }
    }
     */
}
