package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.model.hottub_api.Statistics;
import ax.dkarlsso.hottub.model.RunningTime;
import ax.dkarlsso.hottub.service.OperationsService;
import ax.dkarlsso.hottub.service.RunningTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Basic REST controller for StatisticsApi
 */
@RestController
@RequestMapping("/api")
public class StatisticsController implements ax.dkarlsso.hottub.interfaces.api.hottub_api.StatisticsApi {

    private final RunningTimeService runningTimeService;

    private final OperationsService operationsService;

    @Autowired
    public StatisticsController(final RunningTimeService runningTimeService,
                                final OperationsService operationsService) {
        this.runningTimeService = runningTimeService;
        this.operationsService = operationsService;
    }

    @Override
    public ResponseEntity<Statistics> getStatistics() {
        final RunningTime runningTime = runningTimeService.getRunningTime();
        return ResponseEntity.ok(new Statistics()
                .heaterHours(BigDecimal.valueOf((double) runningTime.getRunningTimeHeater().toMinutes() / 60))
                .circulationPumpHours(BigDecimal.valueOf((double) runningTime.getRunningTimeCirculation().toMinutes() / 60))
                .effectiveBathTimeHours(BigDecimal.valueOf((double) runningTime.getBathTotalTime().toMinutes() / 60))
                .heaterHoursSinceStart(BigDecimal.valueOf((double) operationsService.getOperationalData().getHeaterTimeSinceStarted().toMinutes() / 60)));
    }
}
