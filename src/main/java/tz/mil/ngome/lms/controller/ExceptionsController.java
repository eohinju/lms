package tz.mil.ngome.lms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import tz.mil.ngome.lms.exception.DuplicateDataException;
import tz.mil.ngome.lms.exception.FailureException;
import tz.mil.ngome.lms.exception.InvalidDataException;
import tz.mil.ngome.lms.exception.NotFoundException;
import tz.mil.ngome.lms.exception.UnauthorizedException;
import tz.mil.ngome.lms.utils.Response;
import tz.mil.ngome.lms.utils.ResponseCode;

@ControllerAdvice
public class ExceptionsController {

	@ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<Response<String>> unsupportedMedia(HttpMediaTypeNotSupportedException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.INVALID_MEDIA);
		response.setData("Invalid media");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Response<String>> unsupportedMethod(HttpRequestMethodNotSupportedException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.INVALID_METHOD);
		response.setData("Invalid method");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = InvalidDataException.class)
	public ResponseEntity<Response<String>> invalidData(InvalidDataException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.INVALID_DATA);
		response.setMessage(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = DuplicateDataException.class)
	public ResponseEntity<Response<String>> duplicateData(DuplicateDataException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.DUPLICATE_DATA);
		response.setMessage(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = NotFoundException.class)
	public ResponseEntity<Response<String>> notFound(NotFoundException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.NOT_FOUND);
		response.setMessage(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	public ResponseEntity<Response<String>> notReadable(HttpMessageNotReadableException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.NOT_FOUND);
		response.setMessage(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = UnauthorizedException.class)
	public ResponseEntity<Response<String>> notAuthorized(UnauthorizedException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.NOT_AUTHORIZED);
		response.setMessage(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = FailureException.class)
	public ResponseEntity<Response<String>> failure(FailureException ex){
		Response<String> response = new Response<String>();
		response.setCode(ResponseCode.NOT_AUTHORIZED);
		response.setMessage(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
}