package tz.mil.ngome.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.User;

public interface UserRepository  extends JpaRepository<User, String> {

	Optional<User> findByUsername(String userdata);

	Optional<User> findByMemberId(String id);

}