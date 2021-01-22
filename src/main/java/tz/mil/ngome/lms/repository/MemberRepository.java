package tz.mil.ngome.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

	Optional<Member> findByServiceNumber(String serviceNumber);

	@Query("SELECT new tz.mil.ngome.lms.dto.MemberDto("
			+ "_member.id,_member.compNumber,_member.serviceNumber,_member.rank,_member.firstName,_member.middleName,_member.lastName,"
			+ "_member.phone,_member.unit,_member.subUnit)"
			+ "FROM Member AS _member WHERE _member.deleted=false")
	List<MemberDto> getMembers();

	@Query("SELECT new tz.mil.ngome.lms.dto.MemberDto("
			+ "_member.id,_member.compNumber,_member.serviceNumber,_member.rank,_member.firstName,_member.middleName,_member.lastName,"
			+ "_member.phone,_member.unit,_member.subUnit)"
			+ "FROM Member AS _member WHERE _member.id=:memberId")
	MemberDto findMemberById(String memberId);

	Optional<Member> findByCompNumber(int compNumber);

}
