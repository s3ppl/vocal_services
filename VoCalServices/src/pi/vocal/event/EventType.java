package pi.vocal.event;

/**
 * This {@code Enum} contains all known types an {@code Event} can have up to
 * this point.
 * 
 * @author s3ppl
 * 
 */
public enum EventType {
	// event types
	EXAM("Prüfung"),
	DEMO("Vorführung"),
	SEMINAR("Lehrgang"),
	COMPETITION("Wettkampf"),
	MEETING("Treffen"),
	
	// value if no other type was selected
	NOT_SELECTED(null);

	private String displayName;
	
	private EventType(String name) {
		this.displayName = name;
	}

	public String getName() {
		return displayName;
	}
}
