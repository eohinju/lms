package tz.mil.ngome.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

}
