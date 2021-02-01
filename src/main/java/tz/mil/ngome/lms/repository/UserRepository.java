package tz.mil.ngome.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;

public interface UserRepository  extends JpaRepository<User, String> {

	Optional<User> findByUsername(String userdata);

	Optional<User> findByMemberId(String id);

	@Query(value="SELECT new tz.mil.ngome.lms.dto.UserDto("
			+ "user.id, user.username, user.member.id, user.role)"
			+ "FROM User AS user WHERE user.deleted=false",
			countQuery = " select count(user) from User as user where user.deleted=false"
			)
	Page<UserDto> getUsers(Pageable pageable);
	
	@Query(value="SELECT new tz.mil.ngome.lms.dto.UserDto("
			+ "user.id, user.username, user.member.id, user.role)"
			+ "FROM User AS user WHERE user.deleted=false and user.role<>:role"
			)
	List<UserDto> getUsersNotInRole(Role role);
}