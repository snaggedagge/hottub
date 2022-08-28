package ax.dkarlsso.hottub.controller.rpi;

import ax.dkarlsso.hottub.model.RunningTime;
import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import ax.dkarlsso.hottub.service.RunningTimeService;
import dkarlsso.commons.raspberry.RunningClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class HeaterThread extends Thread {

    private final static Logger log = LoggerFactory.getLogger(HeaterThread.class);

    private final RunningTimeService runningTimeService;

    private final RunningClock circulationClock = new RunningClock();

    private final RunningClock heaterClock = new RunningClock();

    private final RunningClock bathClock = new RunningClock();

    private final OperationsService operationsService;

    private final HeaterInterface heater;

    public HeaterThread(final OperationsService operationsService,
                        final RunningTimeService runningTimeService,
                        final HeaterInterface heaterInterface) {
        super();
        this.operationsService = operationsService;
        this.runningTimeService = runningTimeService;
        heater = heaterInterface;
    }

    @Override
    public void run() {
        log.info("Starting heating thread");
        final Thread thread = Thread.currentThread();
        operationsService.registerSettingsUpdatedAction(thread::interrupt);
        while (true) {
            try {
                heater.loop();
                handleClocks();
                handleTimers();
                Thread.sleep(Duration.ofSeconds(20).toMillis());
            }
            catch (final InterruptedException e) {
                log.info("Thread woken up");
            }
            catch (final Exception e) {
                log.error("Something critical occurred", e);
            }
        }
    }

    private void handleTimers() {
        operationsService.getTimers().forEach(timer -> {
            if (timer.getStartHeatingTime().isBefore(Instant.now())) {
                log.warn("Activating timer");
                operationsService.updateSettings(timer.getSettings());
                operationsService.deleteTimer(timer.getUuid());
            }
        });
    }

    private void handleClocks() {
        final OperationalData operationalData = operationsService.getOperationalData();
        final Settings settings = operationsService.getSettings();
        heaterClock.setStartedRunning(operationalData.isHeating());
        circulationClock.setStartedRunning(operationalData.isCirculating());

        boolean isBathtime = operationalData.getReturnTemp() > 35 && settings.getReturnTempLimit() > 35;
        bathClock.setStartedRunning(isBathtime);

        runningTimeService.saveTime(new RunningTime(heaterClock.getRunningTimeAndReset(),
                circulationClock.getRunningTimeAndReset(), bathClock.getRunningTimeAndReset()));
        operationalData.setHeaterTimeSinceStarted(heaterClock.getTotalRunningTime());
        operationsService.updateOperationalData(operationalData);
    }
}
