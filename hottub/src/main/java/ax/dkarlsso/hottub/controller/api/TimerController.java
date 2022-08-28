package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.model.hottub_api.Timer;
import ax.dkarlsso.hottub.interfaces.model.hottub_api.TimerEntity;
import ax.dkarlsso.hottub.model.settings.TimerSettings;
import ax.dkarlsso.hottub.service.OperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Basic REST controller for TimersApi
 */
@RestController
@RequestMapping("/api")
public class TimerController implements ax.dkarlsso.hottub.interfaces.api.hottub_api.TimersApi {

    private final OperationsService operationsService;

    @Autowired
    public TimerController(final OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @Override
    public ResponseEntity<TimerEntity> addTimer(Timer timer) {
        final TimerSettings timerSettings = MapperUtils.toInternalTimer(timer);
        operationsService.addTimer(timerSettings);
        return ResponseEntity.ok(MapperUtils.toExternalTimer(timerSettings));
    }

    @Override
    public ResponseEntity<Void> deleteTimer(UUID timerId) {
        operationsService.deleteTimer(timerId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TimerEntity>> getTimers() {
        return ResponseEntity.ok(operationsService.getTimers().stream()
                .map(MapperUtils::toExternalTimer)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<TimerEntity> updateTimer(UUID timerId, Timer timer) {
        operationsService.updateTimer(MapperUtils.toInternalTimer(timer, timerId));
        return ResponseEntity.ok(new TimerEntity()
                .id(timerId)
                .time(timer.getTime())
                .settings(timer.getSettings()));
    }
}
