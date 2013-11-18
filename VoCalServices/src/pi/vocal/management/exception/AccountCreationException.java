package pi.vocal.management.exception;

import java.util.Arrays;
import java.util.List;

import pi.vocal.management.ErrorCode;

public class AccountCreationException extends Exception {
	private static final long serialVersionUID = -4343514383021752190L;

	private List<ErrorCode> errorCodes;
	
	public AccountCreationException(Throwable e) {
		super(e);
	}
	
	public AccountCreationException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public AccountCreationException(ErrorCode errorCode) {
		this.errorCodes = Arrays.asList(errorCode);
	}
	
	public AccountCreationException(ErrorCode errorCode, Throwable e) {
		super(e);
		this.errorCodes = Arrays.asList(errorCode);
	}
	
	public AccountCreationException(ErrorCode errorCode, String msg, Throwable e) {
		super(msg, e);
		this.errorCodes = Arrays.asList(errorCode);
	}
	
	public AccountCreationException(List<ErrorCode> errorCodes) {
		this.errorCodes = errorCodes;
	}
	
	public AccountCreationException(List<ErrorCode> errorCodes, String msg) {
		super(msg);
		this.errorCodes = errorCodes;
	}
	
	public AccountCreationException(List<ErrorCode> errorCodes, Throwable e) {
		super(e);
		this.errorCodes = errorCodes;
	}
	
	public AccountCreationException(List<ErrorCode> errorCodes, String msg, Throwable e) {
		super(msg, e);
		this.errorCodes = errorCodes;
	}
	
	public List<ErrorCode> getErrorCodes() {
		return errorCodes;
	}
	
}
