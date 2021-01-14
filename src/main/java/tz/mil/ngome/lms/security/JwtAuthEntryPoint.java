package tz.mil.ngome.lms.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.github.openjson.JSONObject;

import tz.mil.ngome.lms.utils.ResponseCode;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
 
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);
    
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) 
                             throws IOException, ServletException {
      
        logger.error("Unauthorized error. Message - {}", e.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new JSONObject() 
                .put("code", ResponseCode.NOT_AUTHORIZED)
                .put("message", "You are not authorized")
                .toString());
    }
}
