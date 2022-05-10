package hottub.model.dto;

import hottub.model.settings.HeaterDataSettings;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsDTO {
    private int temperature;
    private int heatingPanTemperature;
    private boolean heating;
    private boolean circulating;
    public static StatsDTO from(HeaterDataSettings heaterDataSettings) {
        return StatsDTO.builder()
                .temperature(heaterDataSettings.getReturnTemp())
                .heatingPanTemperature(heaterDataSettings.getOverTemp())
                .heating(heaterDataSettings.isHeating())
                .circulating(heaterDataSettings.isCirculating())
                .build();
    }
}
