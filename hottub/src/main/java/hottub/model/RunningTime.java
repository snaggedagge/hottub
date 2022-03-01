package hottub.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@NoArgsConstructor
public class RunningTime {

    // Identifier used for this class.
    // Since only a single instance of the class ever will be used, use simply default
    public static final String IDENTIFIER = "default";

    private Duration runningTimeHeater = Duration.ofHours(0);

    private Duration runningTimeCirculation = Duration.ofHours(0);

    private Duration bathTotalTime = Duration.ofHours(0);

    public RunningTime(final Duration runningTimeHeater,
                       final Duration runningTimeCirculation,
                       final Duration bathTotalTime) {
        this.runningTimeHeater = runningTimeHeater;
        this.runningTimeCirculation = runningTimeCirculation;
        this.bathTotalTime = bathTotalTime;
    }
}
