package tz.mil.ngome.lms.service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

	@Autowired
	UserRepository userRepo;
	
	@Override
	public User registerUser(UserDto userDto) {
		User user = new User();
		BeanUtils.copyProperties(userDto, user, "id");
		user.setCreatedBy("System");
		User savedUser = userRepo.save(user);
		return savedUser;
	}

}