package pi.vocal.service.helper;

import java.util.ArrayList;
import java.util.List;

import pi.vocal.persistence.dto.Event;
import pi.vocal.persistence.dto.User;
import pi.vocal.service.dto.PublicEvent;
import pi.vocal.service.dto.PublicUser;

public class DtoConversionHelper {

	public static List<PublicUser> usersToPublicUsers(List<User> users) {
		List<PublicUser> publicUsers = new ArrayList<>();
		
		PublicUser publicUser;
		for (User u : users) {
			publicUser = new PublicUser();
			publicUser.setEmail(u.getEmail());
			publicUser.setFirstName(u.getFirstName());
			publicUser.setGrade(u.getGrade());
			publicUser.setLastName(u.getLastName());
			publicUser.setRole(u.getRole());
			publicUser.setSchoolLocation(u.getSchoolLocation());
			
			publicUsers.add(publicUser);
		}
		
		return publicUsers;
	}
	
	public static List<PublicEvent> eventsToPublicEvents(List<Event> events) {
		List<PublicEvent> publicEvents = new ArrayList<>();
		
		PublicEvent publicEvent;
		for (Event e : events) {
			publicEvent = new PublicEvent();
//			publicEvent.set
			
			publicEvents.add(publicEvent);
		}
		
		return null;
	}
	
}
