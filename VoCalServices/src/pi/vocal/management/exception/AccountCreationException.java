package pi.vocal.management.exception;

import pi.vocal.management.ErrorCode;

public class AccountCreationException extends Exception {
	private static final long serialVersionUID = -4343514383021752190L;

	private ErrorCode errorCode;
	
	public AccountCreationException(Throwable e) {
		super(e);
	}
	
	public AccountCreationException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public AccountCreationException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public AccountCreationException(ErrorCode errorCode, Throwable e) {
		super(e);
		this.errorCode = errorCode;
	}
	
	public AccountCreationException(ErrorCode errorCode, String msg, Throwable e) {
		super(msg, e);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
}
