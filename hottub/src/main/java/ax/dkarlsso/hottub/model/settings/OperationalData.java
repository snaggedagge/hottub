package ax.dkarlsso.hottub.model.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OperationalData {
    private int hottubTemperature;

    private int heaterTemperature;

    private boolean heating;

    private boolean circulating;

    public void apply(final OperationalData operationalData) {
        this.hottubTemperature = operationalData.hottubTemperature;
        this.heaterTemperature = operationalData.heaterTemperature;
        this.heating = operationalData.heating;
        this.circulating = operationalData.circulating;
    }

    @Override
    public OperationalData clone() {
        return OperationalData.builder()
                .circulating(this.isCirculating())
                .heating(this.isHeating())
                .heaterTemperature(this.getHeaterTemperature())
                .hottubTemperature(this.getHottubTemperature())
                .build();
    }
}
