package ax.dkarlsso.hottub.model.settings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Settings {

    protected int returnTempLimit = 37;

    /** Temperature diff between surface (wanted temp) and sensor */
    protected int temperatureDiff = 3;

    protected int overTempLimit = returnTempLimit + 5;

    protected boolean lightsOn = true;

    protected boolean debug = false;

    protected Duration circulationTimeCycle = Duration.ofMinutes(3);

    public Settings(final Settings settings) {
        this.lightsOn = settings.lightsOn;
        this.debug = settings.debug;
        this.overTempLimit = settings.overTempLimit;
        this.returnTempLimit = settings.returnTempLimit;
        this.circulationTimeCycle = settings.circulationTimeCycle;
        this.temperatureDiff = settings.temperatureDiff;
    }

    public void applySettings(final Settings settings) {
        this.circulationTimeCycle = settings.getCirculationTimeCycle();
        this.returnTempLimit = settings.getReturnTempLimit();
        this.overTempLimit = settings.getOverTempLimit();
        this.debug = settings.isDebug();
        this.lightsOn = settings.isLightsOn();
        this.temperatureDiff = settings.getTemperatureDiff();
    }

    @Override
    public Settings clone() {
        final Settings settings = new Settings();

        settings.setCirculationTimeCycle(circulationTimeCycle);
        settings.setDebug(debug);
        settings.setOverTempLimit(overTempLimit);
        settings.setReturnTempLimit(returnTempLimit);
        settings.setLightsOn(lightsOn);
        settings.setTemperatureDiff(temperatureDiff);
        return settings;
    }

    public void setReturnTempLimit(final int returnTempLimit) {
        this.returnTempLimit = returnTempLimit;
    }

    public void setOverTempLimit(final int overTempLimit) {
        this.overTempLimit = overTempLimit;
    }

    public void setCirculationTimeCycle(final Duration circulationTimeCycle) {
        this.circulationTimeCycle = circulationTimeCycle;
    }

    @JsonGetter("circulationTimeCycle")
    public int getCirculationTimeCycleMinutes() {
        return (int) circulationTimeCycle.toMinutes();
    }

    @JsonSetter("circulationTimeCycle")
    public void getCirculationTimeCycleMinutes(final int circulationTimeCycle) {
        this.circulationTimeCycle = Duration.ofMinutes(circulationTimeCycle);
    }
}
