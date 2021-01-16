package tz.mil.ngome.lms.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

	private int code;
	private String message = "Success";
	private T data;
	
}
