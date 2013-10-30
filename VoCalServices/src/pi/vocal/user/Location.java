package pi.vocal.user;

public enum Location {

	STUTTGART("Stuttgart"),
	MUEHLACKER("Mühlacker"),
	RAVENSBURG("Ravensburg");
	
	private String name;
	
	private Location(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
