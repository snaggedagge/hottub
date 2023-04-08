package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.interfaces.model.hottub_api.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * Basic controller for REST. Utilized by AWS Alexa, and hopefully a proper frontend if I ever prioritize it
 */
@RestController
@RequestMapping("/api")
public class SettingsController implements ax.dkarlsso.hottub.interfaces.api.hottub_api.SettingsApi {
    private final OperationsService operationsService;

    @Autowired
    public SettingsController(final OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @Override
    public ResponseEntity<Settings> getSettings() {
        final ax.dkarlsso.hottub.model.settings.Settings settings = operationsService.getSettings();
        return ResponseEntity.ok(MapperUtils.toExternalSettings(settings));
    }

    @Override
    public ResponseEntity<Settings> updateSettings(final Settings settings) {
        if(settings.getTemperatureLimit() < 5 && settings.getTemperatureLimit() > 45) {
            throw new IllegalArgumentException("Invalid bounds of temperature: " + settings.getTemperatureLimit());
        }
        if (settings.getHeatingPanTemperatureLimit() > 61 && settings.getHeatingPanTemperatureLimit() < 15) {
            throw new IllegalArgumentException("Invalid bounds of temperature: " + settings.getHeatingPanTemperatureLimit());
        }

        operationsService.updateSettings(ax.dkarlsso.hottub.model.settings.Settings.builder()
                .hottubTemperatureLimit(settings.getTemperatureLimit())
                .temperatureDiff(settings.getTemperatureDelta())
                .heaterTemperatureLimit(settings.getHeatingPanTemperatureLimit())
                .circulationTimeCycle(Duration.ofMinutes(settings.getCirculationTimeCycle()))
                .lightsOn(settings.getLightsOn())
                .debug(settings.getDebugMode())
                .build());
        return getSettings();
    }
}
