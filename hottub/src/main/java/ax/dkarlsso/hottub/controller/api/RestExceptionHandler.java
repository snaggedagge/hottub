package ax.dkarlsso.hottub.controller.api;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = "ax.dkarlsso.hottub.controller.api")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
}
