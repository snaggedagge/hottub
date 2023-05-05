package ax.dkarlsso.hottub.controller.api;

import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class SettingsControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationsService operationsService;

    @Test
    public void getSettings_whenUsingDefaultConfiguration_expectResponseIsOK() throws Exception {
        // Check that response is OK and that values match default configurations
        operationsService.updateSettings(new Settings());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/settings"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperatureLimit").value(37))
                .andExpect(MockMvcResultMatchers.jsonPath("$.heatingPanTemperatureLimit").value(42))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperatureDelta").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.circulationTimeCycle").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lightsOn").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMode").value(false))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void putSettings_whenChangingTemperatureLimit_expectServiceHasUpdatedConfiguration() throws Exception {
        // Check that response is OK and that values match default configurations
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "temperatureLimit", 39,
                                "heatingPanTemperatureLimit", 45,
                                "temperatureDelta", 2,
                                "circulationTimeCycle", 5,
                                "lightsOn", false,
                                "debugMode", true))))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // Verify that the changes actually was transmitted to the internal settings
        Assertions.assertEquals(operationsService.getSettings(), Settings.builder()
                        .hottubTemperatureLimit(39)
                        .heaterTemperatureLimit(45)
                        .temperatureDiff(2)
                        .circulationTimeCycle(Duration.ofMinutes(5))
                        .lightsOn(false)
                        .debug(true)
                .build());
    }


    @Test
    public void putSettings_whenChangingTemperatureAboveLimit_expectBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "temperatureLimit", 50,
                                "heatingPanTemperatureLimit", 45,
                                "temperatureDelta", 2,
                                "circulationTimeCycle", 5,
                                "lightsOn", false,
                                "debugMode", true))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void putSettings_whenChangingTemperatureBelowLimit_expectBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(Map.of(
                                "temperatureLimit", 2,
                                "heatingPanTemperatureLimit", 45,
                                "temperatureDelta", 2,
                                "circulationTimeCycle", 5,
                                "lightsOn", false,
                                "debugMode", true))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
