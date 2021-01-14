package tz.mil.ngome.lms.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2L;
	String message = "";
	
	public NotFoundException(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}