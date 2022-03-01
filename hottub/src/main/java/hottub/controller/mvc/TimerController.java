package hottub.controller.mvc;

import hottub.model.settings.HeaterDataSettings;
import hottub.model.settings.TimerSettings;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TimerController {

    private final HeaterDataSettings heaterDTO;

    @Autowired
    public TimerController(final HeaterDataSettings heaterDTO) {
        this.heaterDTO = heaterDTO;
    }

    @GetMapping("/setTimer")
    public String getTimer(final Model model) {
        TimerSettings timerSettings;
        synchronized (heaterDTO) {
            if(heaterDTO.getTimerSettings() != null) {
                timerSettings = heaterDTO.getTimerSettings();
                model.addAttribute("alreadyOnTimer",true);

                int minutesUntilStart = Minutes.minutesBetween(new DateTime(), timerSettings.getStartHeatingTime()).getMinutes();

                model.addAttribute("minutesUntilStart",minutesUntilStart%60);
                model.addAttribute("hoursUntilStart",minutesUntilStart/60);
            }
            else {
                timerSettings = new TimerSettings();
            }
        }
        model.addAttribute("timer", timerSettings);
        return "timer";
    }

    @PostMapping("/setTimer")
    public String setTimer(final Model model, HttpSession session, @ModelAttribute TimerSettings timerSettings) {

        final List<String> errorList = new ArrayList<>();

        if(timerSettings != null) {
            if(timerSettings.getStartHeatingTime().toDate().getTime() < new Date().getTime()) {
                errorList.add("Need to set a correct date");
            }
            if(errorList.isEmpty()) {
                synchronized (heaterDTO) {
                    model.addAttribute("timerIsSet",true);
                    heaterDTO.setTimerSettings(timerSettings);
                }
            }
        }
        else {
            timerSettings = new TimerSettings();
        }

        model.addAttribute("timer", timerSettings);
        model.addAttribute("errorList",errorList);
        return "timer";
    }

}
