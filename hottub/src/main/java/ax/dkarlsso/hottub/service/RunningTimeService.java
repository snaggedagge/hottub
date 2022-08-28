package ax.dkarlsso.hottub.service;

import ax.dkarlsso.hottub.model.RunningTime;
import dkarlsso.commons.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            runningTime.setBathTotalTime(
                    oldTime.getBathTotalTime().plusNanos(runningTime.getBathTotalTime().toNanos()));
            runningTime.setRunningTimeCirculation(
                    oldTime.getRunningTimeCirculation().plusNanos(runningTime.getRunningTimeCirculation().toNanos()));
            runningTime.setRunningTimeHeater(
                    oldTime.getRunningTimeHeater().plusNanos(runningTime.getRunningTimeHeater().toNanos()));
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