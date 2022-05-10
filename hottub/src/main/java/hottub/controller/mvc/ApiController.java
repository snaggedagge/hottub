package hottub.controller.mvc;

import hottub.model.dto.SettingsDTO;
import hottub.model.dto.StatsDTO;
import hottub.model.settings.HeaterDataSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Basic controller for REST. Utilized by AWS Alexa, and hopefully a proper frontend if I ever prioritize it
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final HeaterDataSettings heaterDTO;

    @Autowired
    public ApiController(HeaterDataSettings heaterDTO) {
        this.heaterDTO = heaterDTO;
    }

    @GetMapping("/stats")
    public StatsDTO getStats() {
        return StatsDTO.from(heaterDTO);
    }

    @GetMapping("/settings")
    public SettingsDTO getSettings() {
        return SettingsDTO.from(heaterDTO);
    }

    @PutMapping("/settings")
    public SettingsDTO updateSettings(@RequestBody final SettingsDTO settingsDTO) {
        synchronized (heaterDTO) {
            heaterDTO.applySettings(SettingsDTO.from(settingsDTO));
        }
        return settingsDTO;
    }
}
