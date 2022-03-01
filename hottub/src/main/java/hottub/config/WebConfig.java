package hottub.config;


import com.pi4j.io.gpio.GpioFactory;
import dkarlsso.commons.raspberry.OSHelper;
import dkarlsso.commons.repository.FileDataRepository;
import hottub.model.settings.HeaterDataSettings;
import hottub.model.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {

    private final LoggerInterceptor loggerInterceptor = new LoggerInterceptor();

    private final HeaterDataSettings heaterDataSettings;

    private final FileDataRepository<Settings> settingsRepository;

    @Autowired
    public WebConfig() {
        if (OSHelper.isRaspberryPi()) {
            GpioFactory.getInstance();
        }
        this.settingsRepository = new FileDataRepository<>(Settings.class, OSHelper.isRaspberryPi()
                ? "/var/opt/brewer/bathtub-settings.json" : "C:\\Users\\Dag Karlsson\\bathtub-settings.json", Settings::new);

        heaterDataSettings = new HeaterDataSettings();
        heaterDataSettings.applySettings(settingsRepository.read());
    }

    @Bean
    public HeaterDataSettings synchronizedHeaterDTO(){
        return heaterDataSettings;
    }

    @Bean
    public FileDataRepository<Settings> settingsFileDataRepository(){
        return settingsRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //log.info("Adding interceptor");
        registry.addInterceptor(loggerInterceptor);
    }
}
