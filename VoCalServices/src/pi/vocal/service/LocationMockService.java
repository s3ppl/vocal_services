package pi.vocal.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import pi.vocal.user.Location;

@Path("/MockService")
public class LocationMockService {

	@Context
	private HttpServletResponse response;
	
	private static final String MEDIA_TYPE_ENCODING = "UTF-8";
	private static final String ENCODED_MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON
			+ ";charset=" + MEDIA_TYPE_ENCODING;

	@GET
	@Path("/getLocations")
//	@Produces(ENCODED_MEDIA_TYPE_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LocationDTO getLocations() throws UnsupportedEncodingException {
		List<String> locList = new ArrayList<>();

		for (Location l : Location.values()) {
			locList.add(new String(l.getName().getBytes(), MEDIA_TYPE_ENCODING));
		}

		Collections.sort(locList);
		LocationDTO locs = new LocationDTO();
		locs.setLocations(locList);
		
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");

		return locs;
	}
}
