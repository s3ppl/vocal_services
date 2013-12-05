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

@Path("/LocationMgmt")
public class LocationService {

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
		
		// set encoding to UTF-8 for compatibility
		response.setCharacterEncoding("UTF-8");

		return locList;
	}
	
	@GET
	@Path("/getEventTypes")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<EventType, String> getEventTypes() throws UnsupportedEncodingException {
		Map<EventType, String> eventTypes = new HashMap<EventType, String>();

		for (EventType et : EventType.values()) {
			eventTypes.put(et, et.getName());
		}
		
		// set encoding to UTF-8 for compatibility
		response.setCharacterEncoding("UTF-8");

		return eventTypes;
	}
}
