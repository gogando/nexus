package thales.nexus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("track")
public class Track {

  @JsonProperty("type")
  public String type;
  
  @JsonProperty("id")
  public String id;  
  
	@JsonProperty("latitude")
	public double latitude;

	@JsonProperty("longitude")
	public double longitude;
	
	@JsonProperty("altitude")
  public double altitude;
  
  @JsonProperty("course")
  public double course;
  
  @JsonProperty("speed")
  public double speed;
  

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
