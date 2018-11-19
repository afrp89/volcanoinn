package controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorHandligController implements ErrorController {

    private final static String ERROR_PATH = "/error";
    private ErrorAttributes errorAttributes;

    public ErrorHandligController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public ModelAndView errorHtml(WebRequest request) {
        return new ModelAndView("/errors/error", getErrorAttributes(request, false));
    }

    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(WebRequest webRequest, HttpServletRequest httpRequest) {
        Map<String, Object> body = getErrorAttributes(webRequest, getTraceParameter(webRequest));
        HttpStatus status = getStatus(httpRequest);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private boolean getTraceParameter(WebRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


}
