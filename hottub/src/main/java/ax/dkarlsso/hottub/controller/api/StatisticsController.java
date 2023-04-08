package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.api.hottub_api.BathDatesApi;
import ax.dkarlsso.hottub.interfaces.model.hottub_api.Statistics;
import ax.dkarlsso.hottub.model.BathDate;
import ax.dkarlsso.hottub.model.RunningTime;
import ax.dkarlsso.hottub.service.OperationsService;
import ax.dkarlsso.hottub.service.RunningTimeService;
import dkarlsso.commons.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Basic REST controller for StatisticsApi
 */
@RestController
@RequestMapping("/api")
public class StatisticsController implements
        ax.dkarlsso.hottub.interfaces.api.hottub_api.StatisticsApi,
        ax.dkarlsso.hottub.interfaces.api.hottub_api.BathDatesApi {

    private final RunningTimeService runningTimeService;

    private final OperationsService operationsService;

    private final CrudRepository<BathDate, String> bathDateLogRepository;

    @Autowired
    public StatisticsController(final RunningTimeService runningTimeService,
                                final OperationsService operationsService,
                                final CrudRepository<BathDate, String> bathDateLogRepository) {
        this.runningTimeService = runningTimeService;
        this.operationsService = operationsService;
        this.bathDateLogRepository = bathDateLogRepository;
    }

    @Override
    public ResponseEntity<Statistics> getStatistics() {
        final RunningTime runningTime = runningTimeService.getRunningTime();
        return ResponseEntity.ok(new Statistics()
                .heaterHours(BigDecimal.valueOf((double) runningTime.getRunningTimeHeater().toMinutes() / 60))
                .circulationPumpHours(BigDecimal.valueOf((double) runningTime.getRunningTimeCirculation().toMinutes() / 60))
                .effectiveBathTimeHours(BigDecimal.valueOf((double) runningTime.getBathTotalTime().toMinutes() / 60))
                .heaterHoursSinceStart(BigDecimal.valueOf((double) runningTimeService.getActiveDurationSinceStart().toMinutes() / 60)));
    }

    @Override
    public ResponseEntity<List<LocalDate>> getBathDates() {
        return ResponseEntity.ok(bathDateLogRepository.findAll().stream()
                .map(BathDate::getDate)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }
}
