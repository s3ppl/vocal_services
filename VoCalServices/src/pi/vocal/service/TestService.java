package pi.vocal.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pi.vocal.persistence.dto.User;

@Path("/TestService")
public class TestService {
	
	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public User getTrackInJSON(@QueryParam("test") String test) {
		
		User track = new User();
		track.setPrename("Enter Sandman");
		track.setSurname("Metallica");
		
		track.setEmail(test);
 
		return track;
	}
	
//	@GET
//	@Path("/getLocations")
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<String> getLocations() {
//		
//		List<String> locList = new ArrayList<String>();
//		locList.add("eins");
//		locList.add("zwei");
//		locList.add("drei");
//		locList.add("vier");
//		
//		return locList;
//	}
	
}
