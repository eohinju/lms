package tz.mil.ngome.lms.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectReturnsDto {

	private String date;
	private String account;
	private MultipartFile file;
	private byte[] bytes;
	
}
