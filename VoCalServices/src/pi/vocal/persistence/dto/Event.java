package pi.vocal.persistence.dto;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import pi.vocal.event.EventType;

/**
 * JavaBean like class, that will be used by Hibernate to store users.
 * 
 * NOTE: Table name has to be manually added, since the used MySql database has issues with capitals.
 *  
 * @author s3ppl
 *
 */

@Entity
@Table(name = "event")
public class Event implements Serializable {

	private static final long serialVersionUID = 3290441828635796018L;

	/**
	 * The date the event starts in its unix-time representation
	 */
	@Column(nullable=false)
	private long startDate;

	/**
	 * The date the event ends in its unix-time representation
	 */
	@Column(nullable=false)
	private long endDate;

	@Column(nullable=false)
	private String title;
	
	/**
	 * Optional description of the event
	 */
	private String description;

	@Column(nullable=false)
	private EventType eventType; 
	
	@Id
	@GeneratedValue
	private long eventId;
	
	@ManyToMany(mappedBy="events")
	private List<User> attendants;

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

	public List<User> getAttendants() {
		return attendants;
	}

	public void setAttendants(List<User> attendants) {
		this.attendants = attendants;
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
	
	
}
