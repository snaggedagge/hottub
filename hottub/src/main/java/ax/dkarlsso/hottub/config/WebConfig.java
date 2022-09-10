package ax.dkarlsso.hottub.config;


import ax.dkarlsso.hottub.model.BathDate;
import ax.dkarlsso.hottub.model.RunningTime;
import ax.dkarlsso.hottub.model.settings.Settings;
import ax.dkarlsso.hottub.service.OperationsService;
import com.pi4j.io.gpio.GpioFactory;
import dkarlsso.commons.raspberry.OSHelper;
import dkarlsso.commons.repository.CrudRepository;
import dkarlsso.commons.repository.FilesystemPersistenceRepository;
import dkarlsso.commons.repository.S3PersistenceRepository;
import dkarlsso.commons.repository.settings.SettingsFilesystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public OperationsService operationsService(final SettingsFilesystemRepository<Settings> settingsRepository){
        return new OperationsService(settingsRepository);
    }

    @Bean
    public SettingsFilesystemRepository<Settings> settingsFileDataRepository(){
        return new SettingsFilesystemRepository<>(Settings.class, OSHelper.isRaspberryPi()
                ? "/var/bath/bathtub-settings.json"
                : "C:\\Users\\dag-k\\bathtub-settings.json", Settings::new);
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
                ? "/var/bath/bath_date_log.json"
                : "C:\\Users\\dag-k\\bath_date_log.json";
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
                ? "/var/bath/running_time.json"
                : "C:\\Users\\dag-k\\running_time.json";
        return new FilesystemPersistenceRepository<>(filepath,
                (runningTime) -> RunningTime.IDENTIFIER, RunningTime.class);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .exposedHeaders("Access-Control-Allow-Origin")
                        .allowedMethods("*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/**.js", "/**.css", "/**.ico", "/**.jpg", "/**.png")
                        .addResourceLocations("classpath:/static/")
                        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));

                registry.addResourceHandler("/assets/**")
                        .addResourceLocations("classpath:/static/assets/")
                        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //log.info("Adding interceptor");
        registry.addInterceptor(new ConnectionLoggerInterceptor());
    }
}
