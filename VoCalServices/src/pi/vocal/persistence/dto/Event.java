package pi.vocal.persistence.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pi.vocal.event.EventType;
import pi.vocal.user.Grade;

/**
 * JavaBean like class, that will be used by Hibernate to store events.
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
	private Long startDate;

	/**
	 * The date the event ends in its unix-time representation
	 */
	@Column(nullable=false)
	private Long endDate;

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
	
	@OneToMany(mappedBy="eventId", fetch=FetchType.EAGER)
	private Set<UserAttendance> userAttendances = new HashSet<>();
	
	@ElementCollection(targetClass=Grade.class, fetch=FetchType.EAGER)
	@CollectionTable(name="event_grades")
	private Set<Grade> attendantsGrades;

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
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

	public Set<UserAttendance> getUserAttendance() {
		return userAttendances;
	}

	public void setUserAttendance(Set<UserAttendance> userAttendances) {
		this.userAttendances = userAttendances;
	}

	public Set<Grade> getAttendantsGrades() {	
		return attendantsGrades;
	}

	public void setAttendantsGrades(Set<Grade> attendantsGrades) {
		this.attendantsGrades = attendantsGrades;
	}
	
	public void addAttendantGrade(Grade grade) {
		this.attendantsGrades.add(grade);
	}
	
	public void addUserAttendance(UserAttendance userAttendances) {	
		this.userAttendances.add(userAttendances);
	}
}
