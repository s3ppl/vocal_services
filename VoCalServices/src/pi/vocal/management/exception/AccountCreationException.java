package pi.vocal.management.exception;

public class AccountCreationException extends Exception {
	private static final long serialVersionUID = -4343514383021752190L;

	public AccountCreationException(Throwable e) {
		super(e);
	}
	
	public AccountCreationException(String msg, Throwable e) {
		super(msg, e);
	}
}
