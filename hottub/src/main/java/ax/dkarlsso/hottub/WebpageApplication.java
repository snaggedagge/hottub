package ax.dkarlsso.hottub;

import com.pi4j.io.gpio.GpioFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class WebpageApplication {

	public static void main(String[] args) {
		GpioFactory.getInstance();
		SpringApplication.run(WebpageApplication.class, args);
	}
}
