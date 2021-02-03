package tz.mil.ngome.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import tz.mil.ngome.lms.dto.UserDto;
import tz.mil.ngome.lms.entity.User;
import tz.mil.ngome.lms.entity.User.Role;

public interface UserRepository  extends JpaRepository<User, String> {

	Optional<User> findByUsername(String userdata);

	Optional<User> findByMemberId(String id);

	@Query(value="SELECT new tz.mil.ngome.lms.dto.UserDto("
			+ "user.id, user.username, user.email, user.member.id, user.role)"
			+ "FROM User AS user WHERE user.deleted=false",
			countQuery = " select count(user) from User as user where user.deleted=false"
			)
	Page<UserDto> getUsers(Pageable pageable);
	
	@Query(value="SELECT new tz.mil.ngome.lms.dto.UserDto("
			+ "user.id, user.username, user.email, user.member.id, user.role)"
			+ "FROM User AS user WHERE user.deleted=false and "
			+ "(cast(user.member.compNumber as text) like %:data% or lower(user.username) like %:data%  or lower(user.member.serviceNumber) like %:data% or lower(user.member.firstName) like %:data% "
			+ "or lower(user.member.middleName) like %:data% or lower(user.member.lastName) like %:data%)"
			)
	List<UserDto> findUsers(String data);
	
	@Query(value="SELECT new tz.mil.ngome.lms.dto.UserDto("
			+ "user.id, user.username, user.email, user.member.id, user.role)"
			+ "FROM User AS user WHERE user.deleted=false and user.role<>:role"
			)
	List<UserDto> getUsersNotInRole(Role role);
	
	@Query(value="SELECT new tz.mil.ngome.lms.dto.UserDto("
			+ "user.id, user.username, user.email, user.member.id, user.role)"
			+ "FROM User AS user WHERE user.deleted=false and user.id=:id"
			)
	UserDto getUser(String id);
	
	@Transactional
	@Modifying
	@Query(value = "update users set role=:newRole where role=:oldRole", nativeQuery = true)
	void setRoleByRole(int newRole, int oldRole);
	
	@Transactional
	@Modifying
	@Query(value = "update users set role=:newRole where role=:oldRole and member_id in(select id from members where subunit=:subUnit)", nativeQuery = true)
	void setRoleByRoleAndSubUnit(int newRole, int oldRole, String subUnit);

	@Query(value = "select email from users where role=:role", nativeQuery = true)
	String findEmailByRole(int role);

	@Query(value = "select email from users where role=:role and subunit=:subUnit", nativeQuery = true)
	String findEmailByRoleAndSubUnit(int role, String subUnit);
	
}