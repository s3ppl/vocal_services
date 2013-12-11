package pi.vocal.management.helper;

/**
 * This {@code enum} contains all constants used by result {@code Map}s as keys.
 * 
 * @author s3ppl
 * 
 */
public enum ResultConstants {
	// edit user result
	EDITUSER_USER_KEY("user"), EDITUSER_SUCCESSCODES_KEY("successcode"),

	// login result
	LOGIN_USER_KEY("user"), LOGIN_SESSIONID_KEY("sessionId"),

	// edit event result
	EDITEVENT_EVENT_KEY("event"), EDITEVENT_SUCCESSCODES_KEY("successcode");

	private String value;

	private ResultConstants(String value) {
		this.value = value;
	}

	/**
	 * Overwrites the toString of {@code Object} to ensure that Jackson uses the
	 * correct names in the JSON parsing of the result {@code Map}s.
	 */
	@Override
	public String toString() {
		return this.value;
	}
}
