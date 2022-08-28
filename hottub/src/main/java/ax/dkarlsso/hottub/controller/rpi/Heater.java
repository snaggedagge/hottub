package ax.dkarlsso.hottub.controller.rpi;


import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import ax.dkarlsso.hottub.utils.CustomTimer;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.TemperatureSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Refactored from C++ Arduino script. Ugly but working for now
 */
public class Heater implements HeaterInterface {
    private final static Logger log = LoggerFactory.getLogger(Heater.class);

    private final CustomTimer timer = new CustomTimer();

    private final OperationsService operationsService;

    private final RelayInterface circulationRelay;
    private final RelayInterface heatingRelay;
    private final RelayInterface lightRelay;

    private final TemperatureSensor heatingPanSensor;
    private final TemperatureSensor returnTemperatureSensor;

    private int heatingTemperatureDelta = 0;
    private int circulationTemperatureDelta = 0;

    /**
     * Dependency Injected constructor
     * @param operationsService operationsService
     * @param overTemp overTemp
     * @param returnTemp returnTemp
     * @param heatingRelay heatingRelay
     * @param circulationRelay circulationRelay
     * @param lightRelay lightRelay
     */
    public Heater(final OperationsService operationsService,
                  final TemperatureSensor overTemp,
                  final TemperatureSensor returnTemp,
                  final RelayInterface heatingRelay,
                  final RelayInterface circulationRelay,
                  final RelayInterface lightRelay) {
        heatingPanSensor = overTemp;
        returnTemperatureSensor = returnTemp;

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
            // TODO: Fix weird debug logging
            log.info("DEBUG:Heating {}", operationalData.isHeating());
            log.info("DEBUG:Circulating {}", operationalData.isCirculating());
            log.info("DEBUG:Circulating based on timer {}", operationalData.isCirculateBasedOnTimer());
            log.info("DEBUG:Lights {}", settings.isLightsOn());
        }
    }


    private void setLogicalOutput(final OperationalData operationalData, final Settings settings){
        if(operationalData.getReturnTemp() + heatingTemperatureDelta < settings.getReturnTempLimit())
        {
            heatingTemperatureDelta = 0;
            operationalData.setHeating(true);
        }
        else {
            heatingTemperatureDelta = 1;
            operationalData.setHeating(false);
        }

        if(operationalData.getOverTemp() + circulationTemperatureDelta > settings.getOverTempLimit())
        {
            circulationTemperatureDelta = 3;
            operationalData.setCirculating(true);
        } else {
            circulationTemperatureDelta = 0;
            operationalData.setCirculating(false);
        }

        // Sanity check to be safe
        if (operationalData.getOverTemp() > settings.getReturnTempLimit() + 10
                && operationalData.getReturnTemp() > settings.getReturnTempLimit() + 10) {
            if (settings.isDebug()) {
                log.warn("Turning off due to much higher temperatures");
            }
            operationalData.setHeating(false);
            operationalData.setCirculating(false);
        }
        this.checkHighTemperatures(operationalData, settings);
        operationalData.setCirculateBasedOnTimer(this.shouldCirculateOnTimer(settings));
        operationalData.setCirculating(operationalData.isCirculating() || operationalData.isCirculateBasedOnTimer());
    }

    @Override
    public void loop() {
        final Settings settings = operationsService.getSettings();
        final OperationalData operationalData = operationsService.getOperationalData();
        try {
            operationalData.setReturnTemp((int) returnTemperatureSensor.readTemp() + settings.getTemperatureDiff());
            if (settings.isDebug()) {
                log.warn("Real temperature is " + (operationalData.getReturnTemp() - settings.getTemperatureDiff()));
            }
            operationalData.setOverTemp((int) heatingPanSensor.readTemp());
            setLogicalOutput(operationalData, settings);
        }
        catch (Exception e) {
            log.error("TEMPERATURE ERROR: " + e.getMessage(), e);
            turnAllOff(settings, operationalData);
        }

        setPhysicalOutput(settings, operationalData);

        operationsService.updateOperationalData(operationalData);
        if(settings.isDebug()) {
            log.info("Actual temperature is {} and heating element temperature is {}",
                    operationalData.getReturnTemp(), operationalData.getOverTemp());
            log.info("Temperature limit is {} and heating element limit is {}",
                    settings.getReturnTempLimit(), settings.getOverTempLimit());
        }
    }

    private void turnAllOff(final Settings settings, final OperationalData operationalData){
        operationalData.setHeating(false);
        operationalData.setCirculating(false);
        setPhysicalOutput(settings, operationalData);
        log.error("TURN ALL OFF");
    }


    private void checkHighTemperatures(final OperationalData operationalData, final Settings settings) {
        if(operationalData.getOverTemp() > 60)
        {
            log.error("Over temp high: {}", operationalData.getOverTemp());
            operationalData.setHeating(false);
            operationalData.setCirculating(true);
        }
        if(operationalData.getReturnTemp() > 40)
        {
            log.error("Return temp high: {}", operationalData.getReturnTemp());
            turnAllOff(settings, operationalData);
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
}
