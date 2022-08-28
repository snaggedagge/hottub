package ax.dkarlsso.hottub.utils;

import java.time.Duration;

public class CustomTimer {

    private long startTime = System.currentTimeMillis()/1000;
    private long elapsedTime = startTime;

    public void reset() {
        startTime = System.currentTimeMillis()/1000;
    }

    public boolean hasTimePassed(Duration duration) {
        elapsedTime = System.currentTimeMillis()/1000;

        return (elapsedTime- startTime) > duration.toSeconds();
    }
}
