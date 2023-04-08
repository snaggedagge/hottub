package ax.dkarlsso.hottub;

import com.pi4j.io.gpio.GpioFactory;
import dkarlsso.commons.raspberry.OSHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class WebpageApplication {

	public static void main(String[] args) {
		if (OSHelper.isRaspberryPi()) {
			GpioFactory.getInstance();
		}
		SpringApplication.run(WebpageApplication.class, args);
	}
}
