package pi.vocal.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pi.vocal.user.Location;

@Path("/MockService")
public class LocationMockService {

	@GET
	@Path("/getLocations")
	@Produces(MediaType.APPLICATION_JSON)
	public LocationDTO getLocations() throws UnsupportedEncodingException {		
		List<String> locList = new ArrayList<>();
		
		for (Location l : Location.values()) {
			locList.add(new String(l.getName().getBytes(), "UTF-8"));
		}
		
		Collections.sort(locList);
		LocationDTO locs = new LocationDTO();
		locs.setLocations(locList);
		
		return locs;
	}
}
