package ax.dkarlsso.hottub;

import com.pi4j.io.gpio.GpioFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebpageApplication {

	public static void main(String[] args) {
		GpioFactory.getInstance();
		SpringApplication.run(WebpageApplication.class, args);
	}
}
