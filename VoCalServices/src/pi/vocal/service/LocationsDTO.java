package pi.vocal.service;

import java.util.List;

import pi.vocal.user.Location;

public class LocationsDTO {

	private List<Location> locations;
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	
}
