package pi.vocal.service.dto;

import java.util.List;

@Deprecated
public class EnumDTO {

	private List<Enum<?>> content;
	
	public List<Enum<?>> getLocations() {
		return content;
	}
	
	public void setLocations(List<Enum<?>> content) {
		this.content = content;
	}
	
}
