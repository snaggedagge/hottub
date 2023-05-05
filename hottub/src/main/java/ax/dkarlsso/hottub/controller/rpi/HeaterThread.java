package ax.dkarlsso.hottub.controller.rpi;

import ax.dkarlsso.hottub.service.OperationsService;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class HeaterThread extends Thread {

    private final OperationsService operationsService;

    private final HeaterInterface heater;

    public HeaterThread(final OperationsService operationsService,
                        final HeaterInterface heaterInterface) {
        super();
        this.operationsService = operationsService;
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

    /**
     * Goes through set timers, and activates them when it is time
     */
    private void handleTimers() {
        operationsService.getTimers().forEach(timer -> {
            if (timer.getStartHeatingTime().isBefore(Instant.now())) {
                log.warn("Activating timer");
                operationsService.updateSettings(timer.getSettings());
                operationsService.deleteTimer(timer.getUuid());
            }
        });
    }
}
