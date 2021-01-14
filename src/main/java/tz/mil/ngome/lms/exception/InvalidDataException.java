package tz.mil.ngome.lms.exception;

public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = 3L;
	String message = "";
	
	public InvalidDataException(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
