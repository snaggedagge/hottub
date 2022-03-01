package hottub.config;

import hottub.model.BathDate;
import hottub.repository.BathDatePersistenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class BathDateScheduledLogger {

    private final int SCHEDULED_ONE_HOUR = 60 * 60 * 1000;

    private LocalDate lastLoggedDate = LocalDate.now();

    private final BathDatePersistenceRepository bathDateLogRepository;

    @Autowired
    public BathDateScheduledLogger(BathDatePersistenceRepository bathDateLogRepository) {
        this.bathDateLogRepository = bathDateLogRepository;
    }

    @Scheduled(fixedDelay = SCHEDULED_ONE_HOUR, initialDelay = 5000)
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
