package ax.dkarlsso.hottub.service;

import ax.dkarlsso.hottub.model.RunningTime;
import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import dkarlsso.commons.raspberry.RunningClock;
import dkarlsso.commons.repository.CrudRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service that works as an hour meter, updating statistics of how long the heater, the circulation pump has been running
 * and the effective bath time
 */
@Service
@AllArgsConstructor
public class RunningTimeService {

    private final CrudRepository<RunningTime, String> runningTimeRepository;

    private final OperationsService operationsService;

    private final RunningClock circulationClock = new RunningClock();

    private final RunningClock heaterClock = new RunningClock();

    private final RunningClock bathClock = new RunningClock();

    @Scheduled(initialDelay = 10000, // 10 seconds
            fixedDelay = 300000) // 5 minutes
    public void recordRunningTime() {
        final OperationalData operationalData = operationsService.getOperationalData();
        final Settings settings = operationsService.getSettings();
        heaterClock.updateState(operationalData.isHeating());
        circulationClock.updateState(operationalData.isCirculating());

        boolean isBathTime = operationalData.getHottubTemperature() > 35 && settings.getHottubTemperatureLimit() > 35;
        bathClock.updateState(isBathTime);

        this.saveTime(new RunningTime(heaterClock.getDurationAndReset(),
                circulationClock.getDurationAndReset(), bathClock.getDurationAndReset()));
    }

    private void saveTime(final RunningTime runningTime) {
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

    public Duration getActiveDurationSinceStart() {
        return heaterClock.getActiveDurationSinceStart();
    }

}
