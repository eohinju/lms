package tz.mil.ngome.lms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CollectedReturnsResponseDto {

	List<MemberPayDto> success;
	List<MemberPayDto> overdeducted;
	List<MemberPayDto> undeducted;
	List<MemberPayDto> nonmember;
	
}
