package pi.vocal.user;

public enum SchoolLocation {

	STUTTGART("Stuttgart"),
	MUEHLACKER("Mühlacker"),
	RAVENSBURG("Ravensburg"),
	
	// TODO remove after added error handling
	NOT_SELECTED(null);
	
	private String name;
	
	private SchoolLocation(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
