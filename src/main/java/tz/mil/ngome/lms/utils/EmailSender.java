package tz.mil.ngome.lms.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.fasterxml.uuid.Logger;

@Component
public class EmailSender {

	@Autowired
    private JavaMailSender emailSender;
	
	public void sendMail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("noreply@baeldung.com");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        try {
        	Logger.logInfo("sending email");
        	emailSender.send(message);
        }catch(Exception e) {
        	Logger.logWarning("Erro sending email: "+e.getMessage());
        }
	}
	
}
