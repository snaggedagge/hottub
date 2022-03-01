package hottub.repository;

import hottub.model.BathDate;
import org.springframework.stereotype.Repository;
import dkarlsso.commons.repository.S3PersistenceRepository;

import java.time.format.DateTimeFormatter;

@Repository
public class BathDatePersistenceRepository extends S3PersistenceRepository<BathDate, String> {

    public BathDatePersistenceRepository() {
        super("stats/bath_date_log.json", "bathtub-statistics",
                (bathDate -> bathDate.getDate().format(DateTimeFormatter.ofPattern(BathDate.DATE_PATTERN))),
                BathDate.class);
    }
}
