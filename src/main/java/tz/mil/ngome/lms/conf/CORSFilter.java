package tz.mil.ngome.lms.conf;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {
 
   @Override
   public void init( FilterConfig filterConfig ) throws ServletException
   {
   }
 
   @Override
   public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain )
      throws IOException, ServletException
   {
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      //String origin = (String) servletRequest.getRemoteHost() + ":" + servletRequest.getRemotePort();
      response.setHeader( "Access-Control-Allow-Origin", "*" );
      response.setHeader( "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS" );
      response.setHeader( "Access-Control-Max-Age", "86400" );
      response.setHeader( "Access-Control-Allow-Headers", "X-requested-with, Authorization, refresh-token, Content-Type" );
      response.setHeader( "Access-Control-Allow-Credentials", "true" );
      filterChain.doFilter( servletRequest, servletResponse );
   }
 
   @Override
   public void destroy()
   {
	   
   }
}