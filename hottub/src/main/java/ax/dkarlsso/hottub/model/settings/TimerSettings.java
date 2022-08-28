package ax.dkarlsso.hottub.model.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class TimerSettings {

    private final UUID uuid;

    private final Settings settings;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private final Instant startHeatingTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerSettings that = (TimerSettings) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
