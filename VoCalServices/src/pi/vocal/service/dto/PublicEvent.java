package pi.vocal.service.dto;

import java.util.List;

import org.codehaus.jackson.annotate.JsonBackReference;

import pi.vocal.event.EventType;

public class PublicEvent {
	private long startDate;
	private long endDate;
	
	private String title;
	private String description;
	
	private EventType eventType; 

	private long eventId;
	
	@JsonBackReference
	private List<PublicUser> attendants;

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public List<PublicUser> getAttendants() {
		return attendants;
	}

	public void setAttendants(List<PublicUser> attendants) {
		this.attendants = attendants;
	}
}
