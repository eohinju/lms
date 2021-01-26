package tz.mil.ngome.lms.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MappedStringListDto {

	public String key;
	public List<String> values = new ArrayList<String>();
	
	public MappedStringListDto(String key) {
		this.key = key;
	}
	
}
