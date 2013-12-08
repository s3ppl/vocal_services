package pi.vocal.persistence.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * JavaBean like class used by Hibernate to map the M:N-relation between
 * {@code User} and {@code Event}.
 * 
 * @author s3ppl
 * 
 */

@Entity
@Table(name = "user_attendance")
public class UserAttendance implements Serializable {
	private static final long serialVersionUID = 160088057122789206L;

	// flag that represents the attendance of the user to the event
	private boolean attends;

	@Id
	@JoinColumn(name = "userId")
	private long userId;

	@Id
	@JoinColumn(name = "eventId")
	private long eventId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public boolean isAttends() {
		return attends;
	}

	public void setAttends(boolean attends) {
		this.attends = attends;
	}

}
