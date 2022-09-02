package ax.dkarlsso.hottub.controller.rpi;


import ax.dkarlsso.hottub.controller.rpi.configurator.OperationsConfigurator;
import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.TemperatureSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Refactored from C++ Arduino script. Ugly but working for now
 */
public class Heater implements HeaterInterface {
    private final static Logger log = LoggerFactory.getLogger(Heater.class);

    private final List<OperationsConfigurator> operationsConfigurators;

    private final OperationsService operationsService;

    private final RelayInterface circulationRelay;
    private final RelayInterface heatingRelay;
    private final RelayInterface lightRelay;

    private final TemperatureSensor heatingPanSensor;
    private final TemperatureSensor returnTemperatureSensor;

    /**
     * Dependency Injected constructor
     *
     * @param operationsConfigurators operationsConfigurators
     * @param operationsService       operationsService
     * @param overTemp                overTemp
     * @param returnTemp              returnTemp
     * @param heatingRelay            heatingRelay
     * @param circulationRelay        circulationRelay
     * @param lightRelay              lightRelay
     */
    public Heater(final List<OperationsConfigurator> operationsConfigurators,
                  final OperationsService operationsService,
                  final TemperatureSensor overTemp,
                  final TemperatureSensor returnTemp,
                  final RelayInterface heatingRelay,
                  final RelayInterface circulationRelay,
                  final RelayInterface lightRelay) {
        this.operationsConfigurators = operationsConfigurators;
        this.heatingPanSensor = overTemp;
        this.returnTemperatureSensor = returnTemp;

        this.circulationRelay = circulationRelay;
        this.heatingRelay = heatingRelay;
        this.lightRelay = lightRelay;
        this.operationsService = operationsService;
    }

    protected void setPhysicalOutput(final Settings settings, final OperationalData operationalData) {
        heatingRelay.setState(operationalData.isHeating());
        circulationRelay.setState(operationalData.isCirculating());
        lightRelay.setState(settings.isLightsOn());
        if(settings.isDebug()) {
            log.debug("Heating {}", operationalData.isHeating());
            log.debug("Circulating {}", operationalData.isCirculating());
            log.debug("Circulating based on timer {}", operationalData.isCirculateBasedOnTimer());
            log.debug("Lights {}", settings.isLightsOn());
        }
    }

    @Override
    public void loop() {
        final Settings settings = operationsService.getSettings();
        final OperationalData operationalData = operationsService.getOperationalData();
        try {
            operationalData.setReturnTemp((int) returnTemperatureSensor.readTemp() + settings.getTemperatureDiff());
            operationalData.setOverTemp((int) heatingPanSensor.readTemp());
            if (settings.isDebug()) {
                // Log temperature without temperature diff
                log.debug("Sensor temperature is " + (operationalData.getReturnTemp() - settings.getTemperatureDiff()));
            }

            operationsConfigurators.forEach(operationsConfigurator ->
                    operationsConfigurator.configure(operationalData, settings));
        }
        catch (final Exception e) {
            log.error("Turning off everything due to: " + e.getMessage(), e);
            turnAllOff(settings, operationalData);
        }
        setPhysicalOutput(settings, operationalData);

        operationsService.updateOperationalData(operationalData);
        if(settings.isDebug()) {
            log.debug("Real temperature is {} and heating element temperature is {}",
                    operationalData.getReturnTemp(), operationalData.getOverTemp());
            log.debug("Temperature limit is {} and heating element limit is {}",
                    settings.getReturnTempLimit(), settings.getOverTempLimit());
        }
    }

    private void turnAllOff(final Settings settings,
                            final OperationalData operationalData){
        operationalData.setHeating(false);
        operationalData.setCirculating(false);
        setPhysicalOutput(settings, operationalData);
        log.error("Turning off everything");
    }
}
