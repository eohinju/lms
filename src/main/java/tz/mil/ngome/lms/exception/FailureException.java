package tz.mil.ngome.lms.exception;

public class FailureException extends RuntimeException {

	private static final long serialVersionUID = 3L;
	String message = "";
	
	public FailureException(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}