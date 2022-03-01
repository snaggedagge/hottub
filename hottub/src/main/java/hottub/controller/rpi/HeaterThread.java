package hottub.controller.rpi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hottub.model.settings.HeaterDataSettings;
import hottub.model.RunningTime;
import hottub.service.RunningTimeService;
import dkarlsso.commons.raspberry.RunningClock;
import hottub.model.settings.TimerSettings;

import java.util.Date;

public class HeaterThread extends Thread{

    private final static Logger log = LoggerFactory.getLogger(HeaterThread.class);

    private final RunningTimeService runningTimeService;

    private final RunningClock circulationClock = new RunningClock();

    private final RunningClock heaterClock = new RunningClock();

    private final RunningClock bathClock = new RunningClock();

    private final HeaterDataSettings heaterDTO;

    private final HeaterInterface heater;

    private boolean running = true;


    public HeaterThread(final HeaterDataSettings heaterDTO, final RunningTimeService runningTimeService, final HeaterInterface heaterInterface) {
        super();
        this.heaterDTO = heaterDTO;
        this.runningTimeService = runningTimeService;
        heater = heaterInterface;
    }


    @Override
    public void run() {
        log.info("Starting heating thread");

        while(running) {
            try {
                heater.loop();

                synchronized (heaterDTO) {
                    heaterClock.setStartedRunning(heaterDTO.isHeating());
                    circulationClock.setStartedRunning(heaterDTO.isCirculating());

                    boolean isBathtime = heaterDTO.getReturnTemp() > 35 && heaterDTO.getReturnTempLimit() > 35;
                    bathClock.setStartedRunning(isBathtime);

                    runningTimeService.saveTime(new RunningTime(heaterClock.getRunningTimeAndReset(),
                            circulationClock.getRunningTimeAndReset(), bathClock.getRunningTimeAndReset()));
                    heaterDTO.setHeaterTimeSinceStarted(heaterClock.getTotalRunningTime());


                    final TimerSettings timerSettings = heaterDTO.getTimerSettings();
                    if(timerSettings != null && timerSettings.getStartHeatingTime().toDate().getTime() < new Date().getTime()) {
                        log.warn("ACTIVATING TIMER");
                        heaterDTO.setReturnTempLimit(timerSettings.getHottubTemperature());
                        heaterDTO.setOverTempLimit(timerSettings.getCirculationTemperature());
                        heaterDTO.setTimerSettings(null);
                    }
                }

                // Only too see if settings changes so they will take affect straight away
                boolean settingsChanged = false;
                for(int i=0;i<20 && !settingsChanged ;i++) {
                    Thread.sleep(500);
                    synchronized (heaterDTO) {
                        settingsChanged = heaterDTO.isSettingsChanged();
                        heaterDTO.setSettingsChanged(false);
                    }
                }
            }
            catch (InterruptedException e) {
            }
        }
        log.info("Ending heating thread");
    }
    @Override
    public void interrupt() {
        running = false;
    }
}
