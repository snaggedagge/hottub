package hottub.model.settings;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;

@EqualsAndHashCode(callSuper = true)
@Data
public class HeaterDataSettings extends Settings {

    private int returnTemp = 0;

    private int overTemp = 0;

    private boolean heating = false;

    private boolean circulating = false;

    private boolean settingsChanged = false;

    private TimerSettings timerSettings = null;

    private Duration heaterTimeSinceStarted = Duration.ofSeconds(0);

    public HeaterDataSettings() {

    }

    public HeaterDataSettings(final Settings settings) {
        super(settings);
    }

    public void applySettings(final Settings settings) {
        this.settingsChanged = true;
        this.circulationTimeCycle = settings.getCirculationTimeCycle();
        this.returnTempLimit = settings.getReturnTempLimit();
        this.overTempLimit = settings.getOverTempLimit();
        this.debug = settings.isDebug();
        this.lightsOn = settings.isLightsOn();
        this.temperatureDiff = settings.getTemperatureDiff();
    }

    public Settings getSettings() {
        return super.clone();
    }

    @Override
    public HeaterDataSettings clone() {
        final HeaterDataSettings heaterDataSettings = new HeaterDataSettings(super.clone());
        heaterDataSettings.returnTemp = returnTemp;
        heaterDataSettings.overTemp = overTemp;
        heaterDataSettings.heating = heating;
        heaterDataSettings.circulating = circulating;
        heaterDataSettings.settingsChanged = settingsChanged;
        heaterDataSettings.timerSettings = timerSettings;

        return heaterDataSettings;
    }
}
