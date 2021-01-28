package tz.mil.ngome.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tz.mil.ngome.lms.dto.MemberDto;
import tz.mil.ngome.lms.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

	Optional<Member> findByServiceNumber(String serviceNumber);

	@Query(value="SELECT new tz.mil.ngome.lms.dto.MemberDto("
			+ "_member.id,_member.compNumber,_member.serviceNumber,_member.rank,_member.firstName,_member.middleName,_member.lastName,"
			+ "_member.phone,_member.unit,_member.subUnit)"
			+ "FROM Member AS _member WHERE _member.deleted=false",
			countQuery = " select count(_member) from Member as _member where _member.deleted=false"
			)
	Page<MemberDto> getMembers(Pageable pageable);
	
//	@Query(value="SELECT new tz.mil.ngome.lms.dto.MemberDto("
//			+ "_member.id,_member.compNumber,_member.serviceNumber,_member.rank,_member.firstName,_member.middleName,_member.lastName,"
//			+ "_member.phone,_member.unit,_member.subUnit)"
//			+ "FROM Member AS _member WHERE _member.deleted=false"
//			)
//	Page<MemberDto> getMembers(Pageable pageable);
	
	@Query(value="SELECT new tz.mil.ngome.lms.dto.MemberDto("
			+ "_member.id,_member.compNumber,_member.serviceNumber,_member.rank,_member.firstName,_member.middleName,_member.lastName,"
			+ "_member.phone,_member.unit,_member.subUnit)"
			+ "FROM Member AS _member WHERE _member.deleted=false and "
			+ "(cast(_member.compNumber as text) like %:data% or lower(_member.serviceNumber) like %:data% or lower(_member.firstName) like %:data% "
			+ "or lower(_member.middleName) like %:data% or lower(_member.lastName) like %:data%)"
			)
	List<MemberDto> findMembers(String data);

	@Query("SELECT new tz.mil.ngome.lms.dto.MemberDto("
			+ "_member.id,_member.compNumber,_member.serviceNumber,_member.rank,_member.firstName,_member.middleName,_member.lastName,"
			+ "_member.phone,_member.unit,_member.subUnit)"
			+ "FROM Member AS _member WHERE _member.id=:memberId")
	MemberDto findMemberById(String memberId);

	Optional<Member> findByCompNumber(int compNumber);

	@Query(value = "select concat(servicenumber,' ',rank,' ',firstname,' ',middlename,' ',lastname) as name from member where id=:id", nativeQuery = true)
	String findNameById(String id);

}
