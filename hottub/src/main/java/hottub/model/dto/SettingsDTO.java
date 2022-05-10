package hottub.model.dto;

import hottub.model.settings.Settings;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettingsDTO {
    private int temperatureLimit;
    private int temperatureDelta;

    private int heatingPanTemperatureLimit;

    private int circulationTimeCycle;

    private boolean lightsOn;
    private boolean debugMode;

    public static SettingsDTO from(Settings settings) {
        return SettingsDTO.builder()
                .temperatureLimit(settings.getReturnTempLimit())
                .heatingPanTemperatureLimit(settings.getOverTempLimit())
                .lightsOn(settings.isLightsOn())
                .debugMode(settings.isDebug())
                .temperatureDelta(settings.getTemperatureDiff())
                .circulationTimeCycle(settings.getCirculationTimeCycle())
                .build();
    }

    public static Settings from(SettingsDTO settingsDTO) {
        final Settings settings = new Settings();
        settings.setDebug(settingsDTO.isDebugMode());
        settings.setLightsOn(settingsDTO.isLightsOn());
        settings.setCirculationTimeCycle(settingsDTO.getCirculationTimeCycle());
        settings.setOverTempLimit(settingsDTO.getHeatingPanTemperatureLimit());
        settings.setTemperatureDiff(settingsDTO.getTemperatureDelta());
        settings.setReturnTempLimit(settingsDTO.getTemperatureLimit());
        return settings;
    }
}
