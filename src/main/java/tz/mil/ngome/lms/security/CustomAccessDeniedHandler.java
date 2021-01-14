package tz.mil.ngome.lms.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.github.openjson.JSONObject;

import tz.mil.ngome.lms.utils.ResponseCode;


public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
      HttpServletRequest request,
      HttpServletResponse response, 
      AccessDeniedException exc) throws IOException, ServletException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new JSONObject() 
                .put("code", ResponseCode.NOT_AUTHORIZED)
                .put("message", "You are not authorized")
                .toString());
    }
}
