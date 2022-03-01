package hottub.controller.mvc;

import dkarlsso.commons.raspberry.OSHelper;
import dkarlsso.commons.repository.settings.SettingsFilesystemRepository;
import hottub.model.settings.HeaterDataSettings;
import hottub.model.RunningTime;
import hottub.service.RunningTimeService;
import hottub.model.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OverviewController {

    private final static Logger log = LoggerFactory.getLogger(OverviewController.class);

    private final RunningTimeService runningTimeService;

    private final HeaterDataSettings heaterDTO;

    private final SettingsFilesystemRepository<Settings> settingsRepository;

    private final Environment environment;

    @Autowired
    public OverviewController(final RunningTimeService runningTimeService,
                              final HeaterDataSettings heaterDTO,
                              final SettingsFilesystemRepository<Settings> settingsRepository,
                              final Environment environment) {
        this.settingsRepository = settingsRepository;
        this.environment = environment;
        log.info("Starting Overview Controller");

        this.runningTimeService = runningTimeService;
        this.heaterDTO = heaterDTO;
    }

    @GetMapping(value = "/")
    public String overview(final ModelMap model) {
        synchronized (heaterDTO) {
            model.addAttribute("settings", heaterDTO.clone());
        }

        final List<String> infoList = new ArrayList<>();
        if (!OSHelper.isRaspberryPi()) {
            infoList.add("This instance is a mocked website, Eg. It is not running on the actual bathtub, so it aint controlling shit");
        }
        if (!environment.acceptsProfiles(Profiles.of("internet-access"))) {
            infoList.add("The bathtub does not seem to have internet access, so it is storing the statistics locally");
        }

        model.addAttribute("infoList", infoList);
        return "overview";
    }

    @PostMapping(value = "/")
    public String overviewPost(final ModelMap model,
                               @ModelAttribute(value = "settings") final HeaterDataSettings settings) {
        if (settings != null) {
            synchronized (heaterDTO) {
                heaterDTO.applySettings(settings);
                settingsRepository.save(settings.getSettings());
            }
        }
        synchronized (heaterDTO) {
            model.addAttribute("settings", heaterDTO.clone());
        }

        return "overview";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(final ModelMap model, HttpSession session, final HttpServletResponse response,
                        final HttpServletRequest request) {
        synchronized (heaterDTO) {
            model.addAttribute("settings", heaterDTO.clone());
        }
        final List<String> errorList = new ArrayList<>();
        errorList.add("Need to have access to do that!");

        model.addAttribute("errorList", errorList);
        return "overview";
    }

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String stats(final ModelMap model) {
        final RunningTime runningTime = runningTimeService.getRunningTime();
        model.addAttribute("runningTimeHeater",
                ((double) runningTime.getRunningTimeHeater().getSeconds()) / 3600);
        model.addAttribute("runningTimeCirculation",
                ((double) runningTime.getRunningTimeCirculation().getSeconds()) / 3600);
        model.addAttribute("bathTotalTime",
                ((double) runningTime.getBathTotalTime().getSeconds()) / 3600);
        synchronized (heaterDTO) {
            model.addAttribute("timeHeatingSinceStarted",
                    ((double) heaterDTO.getHeaterTimeSinceStarted().getSeconds()) / 3600);
        }
        return "stats";
    }
}
