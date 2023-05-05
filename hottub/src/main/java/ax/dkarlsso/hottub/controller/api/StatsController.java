package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.model.hottub_api.Stats;
import ax.dkarlsso.hottub.model.settings.OperationalData;
import ax.dkarlsso.hottub.service.OperationsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic controller for REST. Utilized by AWS Alexa, and hopefully a proper frontend if I ever prioritize it
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class StatsController implements ax.dkarlsso.hottub.interfaces.api.hottub_api.StatsApi {

    private final OperationsService operationsService;

    @Override
    public ResponseEntity<Stats> getStats() {
        final OperationalData operationalData = operationsService.getOperationalData();
        return ResponseEntity.ok(new Stats()
                .circulating(operationalData.isCirculating())
                .heating(operationalData.isHeating())
                .temperature(operationalData.getHottubTemperature())
                .heatingPanTemperature(operationalData.getHeaterTemperature()));
    }
}
