package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.model.hottub_api.Stats;
import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.service.OperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic controller for REST. Utilized by AWS Alexa, and hopefully a proper frontend if I ever prioritize it
 */
@RestController
@RequestMapping("/api")
public class StatsController implements ax.dkarlsso.hottub.interfaces.api.hottub_api.StatsApi {

    private final OperationsService operationsService;

    @Autowired
    public StatsController(final OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @Override
    public ResponseEntity<Stats> getStats() {
        final OperationalData operationalData = operationsService.getOperationalData();
        return ResponseEntity.ok(new Stats()
                .circulating(operationalData.isCirculating())
                .heating(operationalData.isHeating())
                .temperature(operationalData.getReturnTemp())
                .heatingPanTemperature(operationalData.getOverTemp()));
    }
}
