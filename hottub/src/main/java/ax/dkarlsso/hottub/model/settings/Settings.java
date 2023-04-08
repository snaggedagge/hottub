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

    protected int hottubTemperatureLimit = 37;

    /** Temperature diff between surface (wanted temp) and sensor */
    protected int temperatureDiff = 3;

    protected int heaterTemperatureLimit = hottubTemperatureLimit + 5;

    protected boolean lightsOn = true;

    protected boolean debug = false;

    protected Duration circulationTimeCycle = Duration.ofMinutes(3);

    public void applySettings(final Settings settings) {
        this.circulationTimeCycle = settings.getCirculationTimeCycle();
        this.hottubTemperatureLimit = settings.getHottubTemperatureLimit();
        this.heaterTemperatureLimit = settings.getHeaterTemperatureLimit();
        this.debug = settings.isDebug();
        this.lightsOn = settings.isLightsOn();
        this.temperatureDiff = settings.getTemperatureDiff();
    }

    @Override
    public Settings clone() {
        final Settings settings = new Settings();

        settings.setCirculationTimeCycle(circulationTimeCycle);
        settings.setDebug(debug);
        settings.setHeaterTemperatureLimit(heaterTemperatureLimit);
        settings.setHottubTemperatureLimit(hottubTemperatureLimit);
        settings.setLightsOn(lightsOn);
        settings.setTemperatureDiff(temperatureDiff);
        return settings;
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
