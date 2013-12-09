package pi.vocal.user;

public enum SchoolLocation {

	// currently known locations and their display text
	STUTTGART("Stuttgart"),
	MUEHLACKER("Mühlacker"),
	RAVENSBURG("Ravensburg"),
	
	// if no location was selected
	NOT_SELECTED(null);
	
	private String name;
	
	private SchoolLocation(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
