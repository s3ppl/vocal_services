package pi.vocal.service.tests;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import pi.vocal.user.SchoolLocation;

@Path("/MockService")
public class LocationMockService {

	@Context
	private HttpServletResponse response;
	
	@GET
	@Path("/getLocations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<SchoolLocation, String> getLocations() throws UnsupportedEncodingException {
		Map<SchoolLocation, String> locList = new HashMap<SchoolLocation, String>();

		for (SchoolLocation l : SchoolLocation.values()) {
			locList.put(l, l.getName());
		}
		
		response.setCharacterEncoding("UTF-8");

		return locList;
	}
}
