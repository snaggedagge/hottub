package ax.dkarlsso.hottub.controller.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Primary
public class ErrorController extends AbstractErrorController {

    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    // Redirect all unknown pages/content to angular application
    @RequestMapping("/error")
    public String error(final HttpServletRequest request, final HttpServletResponse response) {
        if (HttpStatus.NOT_FOUND == getStatus(request)) {
            response.setStatus(200);
            return "index.html";
        }
        return null;
    }
}