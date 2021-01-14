package tz.mil.ngome.lms.security;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    UserRepository userRepository;
	
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userdata)
            throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(userdata);
        if(!user.isPresent())
                  throw(new UsernameNotFoundException("User Not Found with -> username or email : " + userdata));
        return UserPrinciple.build(user.get());
    }
    
    public User currentUser() {
    	String username;
    	Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if(principle instanceof UserDetails)
    		username = ((UserDetails) principle).getUsername();
    	else
    		username = principle.toString();
    	User user = userRepository.findByUsername(username).get();
    	return user;
    }
}
