package pi.vocal.service.tests;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
	
	@Context
	private HttpServletRequest request;
	
	@GET
	@Path("/getLocations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<Location, String> getLocations() throws UnsupportedEncodingException {
		Map<Location, String> locList = new HashMap<Location, String>();

		for (Location l : Location.values()) {
			locList.put(l, l.getName());
		}
		
		response.setCharacterEncoding("UTF-8");

		return locList;
	}
	
	@GET
	@Path("/getRequest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRequest() throws UnsupportedEncodingException {
		request.getSession().setAttribute("foo", "bar");
		return request.getSession().getId();
	}
}
