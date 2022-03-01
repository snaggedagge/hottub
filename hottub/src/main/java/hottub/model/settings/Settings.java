package hottub.model.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Settings {

    protected int returnTempLimit = 37;

    /** Temperature diff between surface (wanted temp) and sensor */
    protected int temperatureDiff = 3;

    protected int overTempLimit = returnTempLimit + 5;

    protected boolean lightsOn = true;

    protected boolean debug = false;

    protected int circulationTimeCycle = 3;

    public Settings(final Settings settings) {
        this.lightsOn = settings.lightsOn;
        this.debug = settings.debug;
        this.overTempLimit = settings.overTempLimit;
        this.returnTempLimit = settings.returnTempLimit;
        this.circulationTimeCycle = settings.circulationTimeCycle;
        this.temperatureDiff = settings.temperatureDiff;
    }

    @Override
    protected Settings clone() {
        final Settings settings = new Settings();

        settings.setCirculationTimeCycle(circulationTimeCycle);
        settings.setDebug(debug);
        settings.setOverTempLimit(overTempLimit);
        settings.setReturnTempLimit(returnTempLimit);
        settings.setLightsOn(lightsOn);
        settings.setTemperatureDiff(temperatureDiff);
        return settings;
    }
}
