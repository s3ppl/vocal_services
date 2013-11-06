package pi.vocal.event;

public enum EventType {
	EXAM("Prüfung"),
	DEMO("Vorführung"),
	SEMINAR("Lehrgang"),
	COMPETITION("Wettkampf"),
	MEETING("Treffen");

	private String name;

	private EventType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
