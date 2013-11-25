package pi.vocal.user;

public enum SchoolLocation {

	STUTTGART("Stuttgart"),
	MUEHLACKER("Mühlacker"),
	RAVENSBURG("Ravensburg");
	
	private String name;
	
	private SchoolLocation(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
