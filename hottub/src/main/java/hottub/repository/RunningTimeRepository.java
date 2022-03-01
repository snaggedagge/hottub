package hottub.repository;

import org.springframework.stereotype.Repository;
import dkarlsso.commons.repository.S3PersistenceRepository;

@Repository
public class RunningTimeRepository extends S3PersistenceRepository<RunningTime, String> {

    public RunningTimeRepository() {
        super("stats/running_time.json", "bathtub-statistics",
                (runningTime) -> RunningTime.IDENTIFIER, RunningTime.class);
    }
}
