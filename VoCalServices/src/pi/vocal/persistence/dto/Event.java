package pi.vocal.persistence.dto;

import java.io.Serializable;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

public class Event implements Serializable {

	private static final long serialVersionUID = 3290441828635796018L;

	private long startDate;
	private long endDate;
	
	@Id
	@GeneratedValue
	private long eventId;
	
	@ManyToMany
	@JoinColumn(name="userId")
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
	
}
