package pi.vocal.service;

import java.util.List;

import pi.vocal.user.SchoolLocation;


// TODO make me generic for enum usage
public class SchoolLocationsDTO {

	private List<SchoolLocation> locations;
	
	public List<SchoolLocation> getLocations() {
		return locations;
	}
	
	public void setLocations(List<SchoolLocation> locations) {
		this.locations = locations;
	}
	
}
