package ax.dkarlsso.hottub.controller.rpi;


import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import ax.dkarlsso.hottub.utils.CustomTimer;
import dkarlsso.commons.raspberry.enums.GPIOPins;
import dkarlsso.commons.raspberry.relay.OptoRelay;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;
import dkarlsso.commons.raspberry.sensor.temperature.DS18B20;
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
     * Real implementation. Terrible i know, should be dependency injected everything, but im lazy
     * @param operationsService operationsService
     */
    public Heater(final OperationsService operationsService) {
        returnTemperatureSensor = new DS18B20("28-030c979428d4");
        heatingPanSensor = new DS18B20("28-030297942385");

        this.operationsService = operationsService;

        lightRelay = new OptoRelay(GPIOPins.GPIO19);
        heatingRelay = new OptoRelay(GPIOPins.GPIO13);
        circulationRelay = new OptoRelay(GPIOPins.GPIO26);

    }

    /**
     * For simplified unittesting...
     * @param overTemp
     * @param returnTemp
     * @param operationsService
     * @param heatingRelay
     * @param circulationRelay
     * @param lightRelay
     */
    public Heater(TemperatureSensor overTemp, TemperatureSensor returnTemp, final OperationsService operationsService,
                  RelayInterface heatingRelay,RelayInterface circulationRelay,RelayInterface lightRelay) {
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
        } catch (Exception e) {
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
