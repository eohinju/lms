package tz.mil.ngome.lms.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.mil.ngome.lms.entity.Account;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectReturnsDto {

	private String date;
	private String account;
	private MultipartFile file;
	
}
