package hottub.service;

import dkarlsso.commons.repository.CrudRepository;
import hottub.model.RunningTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RunningTimeService {

    private final CrudRepository<RunningTime, String> runningTimeRepository;

    @Autowired
    public RunningTimeService(final CrudRepository<RunningTime, String> runningTimeRepository) {
        this.runningTimeRepository = runningTimeRepository;
    }

    public void saveTime(final RunningTime runningTime) {
        if(runningTimeRepository.exists(RunningTime.IDENTIFIER)) {
            final RunningTime oldTime = runningTimeRepository.findById(RunningTime.IDENTIFIER);
            runningTime.setBathTotalTime(Duration.ofNanos(oldTime.getBathTotalTime().toNanos()
                    + runningTime.getBathTotalTime().toNanos()));
            runningTime.setRunningTimeCirculation(Duration.ofNanos(oldTime.getRunningTimeCirculation().toNanos()
                    + runningTime.getRunningTimeCirculation().toNanos()));
            runningTime.setRunningTimeHeater(Duration.ofNanos(oldTime.getRunningTimeHeater().toNanos()
                    + runningTime.getRunningTimeHeater().toNanos()));
            runningTimeRepository.save(runningTime);
        }
        else {
            runningTimeRepository.save(runningTime);
        }
    }

    public RunningTime getRunningTime() {
        // Same id, supposed to be only one post
        final RunningTime runningTime = new RunningTime();
        if (!runningTimeRepository.exists(RunningTime.IDENTIFIER)) {
            this.saveTime(runningTime);
            return runningTime;
        }
        return runningTimeRepository.findById(RunningTime.IDENTIFIER);
    }

}
