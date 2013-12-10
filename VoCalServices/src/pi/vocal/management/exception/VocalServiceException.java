package pi.vocal.management.exception;

import java.util.Arrays;
import java.util.List;

import pi.vocal.management.returncodes.ErrorCode;

/**
 * Basic {@code Exception} for all classes used in this project.
 * 
 * @author s3ppl
 * 
 */
public class VocalServiceException extends Exception {
	private static final long serialVersionUID = -4343514383021752190L;

	// List of error codes that contain all errors that have occured, e.g. while
	// creating a user
	private List<ErrorCode> errorCodes;
	
	public List<ErrorCode> getErrorCodes() {
		return errorCodes;
	}

	public VocalServiceException(Throwable e) {
		super(e);
	}

	public VocalServiceException(String msg, Throwable e) {
		super(msg, e);
	}

	public VocalServiceException(ErrorCode errorCode) {
		this.errorCodes = Arrays.asList(errorCode);
	}

	public VocalServiceException(ErrorCode errorCode, Throwable e) {
		super(e);
		this.errorCodes = Arrays.asList(errorCode);
	}

	public VocalServiceException(ErrorCode errorCode, String msg, Throwable e) {
		super(msg, e);
		this.errorCodes = Arrays.asList(errorCode);
	}

	public VocalServiceException(List<ErrorCode> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public VocalServiceException(List<ErrorCode> errorCodes, String msg) {
		super(msg);
		this.errorCodes = errorCodes;
	}

	public VocalServiceException(List<ErrorCode> errorCodes, Throwable e) {
		super(e);
		this.errorCodes = errorCodes;
	}

	public VocalServiceException(List<ErrorCode> errorCodes, String msg,
			Throwable e) {
		
		super(msg, e);
		this.errorCodes = errorCodes;
	}

}
