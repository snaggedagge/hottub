package hottub.config;


import com.pi4j.io.gpio.GpioFactory;
import dkarlsso.commons.raspberry.OSHelper;
import dkarlsso.commons.repository.CrudRepository;
import dkarlsso.commons.repository.FilesystemPersistenceRepository;
import dkarlsso.commons.repository.S3PersistenceRepository;
import dkarlsso.commons.repository.settings.SettingsFilesystemRepository;
import hottub.model.BathDate;
import hottub.model.settings.HeaterDataSettings;
import hottub.model.settings.Settings;
import hottub.model.RunningTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;



@Configuration
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {

    private final LoggerInterceptor loggerInterceptor = new LoggerInterceptor();

    private final HeaterDataSettings heaterDataSettings;

    private final SettingsFilesystemRepository<Settings> settingsRepository;

    @Autowired
    public WebConfig() {
        if (OSHelper.isRaspberryPi()) {
            GpioFactory.getInstance();
        }
        this.settingsRepository = new SettingsFilesystemRepository<>(Settings.class, OSHelper.isRaspberryPi()
                ? "/var/opt/brewer/bathtub-settings.json"
                : "C:\\Users\\Dag Karlsson\\bathtub-settings.json", Settings::new);

        heaterDataSettings = new HeaterDataSettings();
        heaterDataSettings.applySettings(settingsRepository.read());
    }

    @Bean
    public HeaterDataSettings synchronizedHeaterDTO(){
        return heaterDataSettings;
    }

    @Bean
    public SettingsFilesystemRepository<Settings> settingsFileDataRepository(){
        return settingsRepository;
    }

    @Profile("internet-access")
    @Primary
    @Bean
    public CrudRepository<BathDate, String> bathDateS3PersistenceRepository() {
        return new S3PersistenceRepository<>("stats/bath_date_log.json", "bathtub-statistics",
                (bathDate -> bathDate.getDate().format(DateTimeFormatter.ofPattern(BathDate.DATE_PATTERN))),
                BathDate.class);
    }

    @Bean
    public CrudRepository<BathDate, String> bathDateFilesystemPersistenceRepository() {
        final String filepath = OSHelper.isRaspberryPi()
                ? "/var/opt/brewer/bath_date_log.json"
                : "C:\\Users\\Dag Karlsson\\bath_date_log.json";
        return new FilesystemPersistenceRepository<>(filepath,
                (bathDate -> bathDate.getDate().format(DateTimeFormatter.ofPattern(BathDate.DATE_PATTERN))),
                BathDate.class);
    }


    @Profile("internet-access")
    @Primary
    @Bean
    public CrudRepository<RunningTime, String> runningTimeS3Repository() {
        return new S3PersistenceRepository<>("stats/running_time.json", "bathtub-statistics",
                (runningTime) -> RunningTime.IDENTIFIER, RunningTime.class);
    }

    @Bean
    public CrudRepository<RunningTime, String> runningTimeFilesystemRepository() {
        final String filepath = OSHelper.isRaspberryPi()
                ? "/var/opt/brewer/running_time.json"
                : "C:\\Users\\Dag Karlsson\\running_time.json";
        return new FilesystemPersistenceRepository<>(filepath,
                (runningTime) -> RunningTime.IDENTIFIER, RunningTime.class);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //log.info("Adding interceptor");
        registry.addInterceptor(loggerInterceptor);
    }
}
