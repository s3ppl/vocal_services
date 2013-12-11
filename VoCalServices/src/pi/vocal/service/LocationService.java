package pi.vocal.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import pi.vocal.event.EventType;
import pi.vocal.user.SchoolLocation;

/**
 * This class contains a single WebService that is used by the front end to load
 * all {@code SchoolLocation}s used in the system dynamically.
 * 
 * @author s3ppl
 * 
 */
@Path("/LocationMgmt")
public class LocationService {

	/**
	 * Actual response send by the server. Filled by injection.
	 */
	@Context
	private HttpServletResponse response;

	/**
	 * WebService that returns all used {@code SchoolLocation}s used in the
	 * system as {@code Map}. For compatibility reasons, the response will be
	 * encoded in "UTF-8".
	 * 
	 * @return Returns a {@code Map} of all {@code SchoolLocation}s
	 * @throws UnsupportedEncodingException
	 *             Thrown if the character encoding to "UTF-8" fails
	 */
	@GET
	@Path("/getLocations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<SchoolLocation, String> getLocations() throws UnsupportedEncodingException {
		Map<SchoolLocation, String> locList = new HashMap<SchoolLocation, String>();

		for (SchoolLocation l : SchoolLocation.values()) {
			locList.put(l, l.getName());
		}

		// set encoding to UTF-8 for compatibility
		response.setCharacterEncoding("UTF-8");

		return locList;
	}
}
