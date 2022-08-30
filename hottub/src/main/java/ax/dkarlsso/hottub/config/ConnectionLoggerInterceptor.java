package ax.dkarlsso.hottub.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ConnectionLoggerInterceptor implements HandlerInterceptor {
    Logger remoteConnectionsLog = LoggerFactory.getLogger("connections");
    Logger localConnectionsLog = LoggerFactory.getLogger("local-connections");


    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,final Object handler) throws Exception {
        String ipAddr = getRemoteAddr(request);
        final Logger log;
        if(ipAddr.contains("127.0") || ipAddr.contains("0:0:0") ||  ipAddr.contains("192.168.1") ) {
            log = localConnectionsLog;
        } else {
            log = remoteConnectionsLog;
        }
        log.trace("Requestlogger : " + " Method: [" + request.getMethod()
                + "] URL: [" + request.getRequestURI()+"] Ipadress: [" + ipAddr+"]" );
        return true;
    }

    private String getRemoteAddr(final HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (StringUtils.isNotBlank(ipFromHeader)) {
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }
}