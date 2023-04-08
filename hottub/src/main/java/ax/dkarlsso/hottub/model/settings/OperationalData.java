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
    private int returnTemp;

    private int overTemp;

    private boolean heating;

    private boolean circulating;

    public void apply(final OperationalData operationalData) {
        this.returnTemp = operationalData.returnTemp;
        this.overTemp = operationalData.overTemp;
        this.heating = operationalData.heating;
        this.circulating = operationalData.circulating;
    }

    @Override
    public OperationalData clone() {
        return OperationalData.builder()
                .circulating(this.isCirculating())
                .heating(this.isHeating())
                .overTemp(this.getOverTemp())
                .returnTemp(this.getReturnTemp())
                .build();
    }
}
