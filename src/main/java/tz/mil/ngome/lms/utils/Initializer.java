package tz.mil.ngome.lms.utils;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;
import tz.mil.ngome.lms.repository.UserRepository;
import tz.mil.ngome.lms.security.JwtAuthTokenFilter;

@Component
public class Initializer {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
    PasswordEncoder encoder;
	
	@PostConstruct
	public void init() {
		initializeUser();
	}
	
	private void initializeUser() {
		User user = new User("root",encoder.encode("toor"),Role.ROLE_ADMIN,null);
		user.setCreatedBy("Initializer");
		try {
			userRepo.save(user);
			logger.info("root seeded");
		}catch(Exception e) {
			logger.warn("root already seeded");
		}
	}

}
