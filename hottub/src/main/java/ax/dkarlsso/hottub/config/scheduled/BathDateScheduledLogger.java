package ax.dkarlsso.hottub.config.scheduled;

import ax.dkarlsso.hottub.model.BathDate;
import dkarlsso.commons.repository.CrudRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class BathDateScheduledLogger {

    private LocalDate lastLoggedDate = LocalDate.now();

    private final CrudRepository<BathDate, String> bathDateLogRepository;

    @Autowired
    public BathDateScheduledLogger(CrudRepository<BathDate, String> bathDateLogRepository) {
        this.bathDateLogRepository = bathDateLogRepository;
    }

    @Scheduled(fixedDelay = 1, initialDelayString = "PT5S", timeUnit = TimeUnit.HOURS)
    public void scheduleFixedDelayTask() {
        lastLoggedDate = LocalDate.now();

        AtomicBoolean dateIsAlreadyLogged = new AtomicBoolean(false);
        bathDateLogRepository.findAll().forEach(bathDate -> {
            if (bathDate.getDate().isEqual(lastLoggedDate)) {
                dateIsAlreadyLogged.set(true);
            }
        });

        if (!dateIsAlreadyLogged.get()) {
            log.info("Saving date to log of baths");
            bathDateLogRepository.save(new BathDate(lastLoggedDate));
        }
    }

}
