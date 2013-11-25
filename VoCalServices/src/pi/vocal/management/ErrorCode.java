package pi.vocal.management;

public enum ErrorCode {

	// account creation errors
	PASSWORD_MISSING,
	PASSWORD_TOO_SHORT,
	PASSWORDS_DONT_MATCH,
	FIRSTNAME_MISSING,
	LASTNAME_MISSING,
	GRADE_MISSING,
	SCHOOL_LOCATION_MISSING,
	EMAIL_MISSING,
	EMAIL_ALREADY_IN_USE,
	EMAIL_INVALID,
	
	// misc
	INTERNAL_ERROR,
	
	// login errors
	AUTHENTICATION_FAILED;
	
//	private String displayText;
//	
//	public ErrorCode(String displayText) {
//		this.displayText = displayText;
//	}
//	
//	public String getDisplayText() {
//		return displayText;
//	}
}
