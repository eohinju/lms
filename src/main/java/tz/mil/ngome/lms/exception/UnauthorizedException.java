package tz.mil.ngome.lms.exception;

public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	String message = "";
	
	public UnauthorizedException(){
		this.message = "Sorry, you are not authorized";
	}
	
	public UnauthorizedException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}