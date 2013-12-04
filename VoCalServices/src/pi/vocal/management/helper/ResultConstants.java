package pi.vocal.management.helper;

public enum ResultConstants {
	EDITUSER_USER_KEY("user"),
	EDITUSER_SUCCESSCODES_KEY("successcode"),
	LOGIN_USER_KEY("user"),
	LOGIN_SESSIONID_KEY("sessionId");
	
	private String value;
	
	private ResultConstants(String value) {
		this.value = value;
	}
	
	public String toString() {
		return this.value;
	}
}
