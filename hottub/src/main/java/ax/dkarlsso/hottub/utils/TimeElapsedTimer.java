package ax.dkarlsso.hottub.utils;

import java.time.Duration;
import java.time.Instant;

public class TimeElapsedTimer {
    private Instant startTime = Instant.now();

    public void reset() {
        startTime = Instant.now();
    }

    public boolean hasTimePassed(Duration duration) {
        final Instant elapsedTime = Instant.now();
        return Duration.between(startTime, elapsedTime).toSeconds() > duration.toSeconds();
    }
}
