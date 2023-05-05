package ax.dkarlsso.hottub.controller.api

import ax.dkarlsso.hottub.model.settings.Settings
import ax.dkarlsso.hottub.service.OperationsService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import java.time.Duration

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test with groovy and spock for fun. Kinda cool syntax but tests become quite a lot longer
 */
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerSpockTest extends Specification {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired MockMvc mockMvc;

    @Autowired OperationsService operationsService;

    def "GET /api/settings should return OK with default values"() {
        given:
        operationsService.updateSettings(new Settings());

        when:
        def results = mockMvc.perform(MockMvcRequestBuilders.get("/api/settings"))

        then:
        results.andExpect(status().isOk())

        and:
        results.andExpect(jsonPath('$.temperatureLimit').value(37))
                .andExpect(jsonPath('$.heatingPanTemperatureLimit').value(42))
                .andExpect(jsonPath('$.temperatureDelta').value(3))
                .andExpect(jsonPath('$.circulationTimeCycle').value(3))
                .andExpect(jsonPath('$.lightsOn').value(true))
                .andExpect(jsonPath('$.debugMode').value(false))
    }

    def "PUT /api/settings should return OK with default values"() {
        given:
        Map request = [
                temperatureLimit            : 39,
                heatingPanTemperatureLimit  : 45,
                temperatureDelta            : 2,
                circulationTimeCycle        : 5,
                lightsOn                    : false,
                debugMode                   : true
        ]
        and:
        operationsService.updateSettings(new Settings());
        when:
        def results = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(request)))
        then:
        results.andExpect(status().isOk())
        and:
        Assertions.assertEquals(operationsService.getSettings(), Settings.builder()
                .hottubTemperatureLimit(39)
                .heaterTemperatureLimit(45)
                .temperatureDiff(2)
                .circulationTimeCycle(Duration.ofMinutes(5))
                .lightsOn(false)
                .debug(true)
                .build());
    }

    def "PUT /api/settings with limits above bounds responds bad request"() {
        given:
        Map request = [
                temperatureLimit            : 50,
                heatingPanTemperatureLimit  : 45,
                temperatureDelta            : 2,
                circulationTimeCycle        : 5,
                lightsOn                    : false,
                debugMode                   : true
        ]
        when:
        def results = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(request)))

        then:
        results.andExpect(status().isBadRequest())
    }

    def "PUT /api/settings with limits below bounds responds bad request"() {
        given:
        Map request = [
                temperatureLimit            : 2,
                heatingPanTemperatureLimit  : 45,
                temperatureDelta            : 2,
                circulationTimeCycle        : 5,
                lightsOn                    : false,
                debugMode                   : true
        ]
        when:
        def results = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(request)))

        then:
        results.andExpect(status().isBadRequest())
    }
}
