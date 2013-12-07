package pi.vocal.management.helper;

public enum ResultConstants {
	EDITUSER_USER_KEY("user"),
	EDITUSER_SUCCESSCODES_KEY("successcode"),
	LOGIN_USER_KEY("user"),
	LOGIN_SESSIONID_KEY("sessionId"),
	EDITEVENT_EVENT_KEY("event"),
	EDITEVENT_SUCCESSCODES_KEY("successcode");
	
	private String value;
	
	private ResultConstants(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
