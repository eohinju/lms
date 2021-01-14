package tz.mil.ngome.lms.exception;

public class DuplicateDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	String message = "";
	
	public DuplicateDataException(String message){
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
