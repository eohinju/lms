package tz.mil.ngome.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.User;

public interface UserRepository  extends JpaRepository<User, String> {

}