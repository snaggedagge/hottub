package ax.dkarlsso.hottub.model.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OperationalData {
    private int returnTemp = 0;

    private int overTemp = 0;

    private boolean heating = false;

    private boolean circulating = false;

    private boolean circulateBasedOnTimer = false;

    private Duration heaterTimeSinceStarted = Duration.ofSeconds(0);


    public void apply(final OperationalData operationalData) {
        this.returnTemp = operationalData.returnTemp;
        this.overTemp = operationalData.overTemp;
        this.heating = operationalData.heating;
        this.circulating = operationalData.circulating;
        this.circulateBasedOnTimer = operationalData.circulateBasedOnTimer;
        this.heaterTimeSinceStarted = operationalData.heaterTimeSinceStarted;
    }

    @Override
    public OperationalData clone() {
        return OperationalData.builder()
                .circulating(this.isCirculating())
                .heating(this.isHeating())
                .heaterTimeSinceStarted(this.getHeaterTimeSinceStarted())
                .overTemp(this.getOverTemp())
                .returnTemp(this.getReturnTemp())
                .build();
    }
}
