package pi.vocal.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pi.vocal.service.dto.PublicEvent;

@Path("/EventMgmt")
public class EventService {

	@GET
	//@POST
	@Path("/getEventById")
	@Produces(MediaType.APPLICATION_JSON)
	public PublicEvent getEventById(@QueryParam("id") long id) {
		return null;
	}
	
}
