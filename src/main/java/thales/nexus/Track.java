package thales.nexus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("track")
public class Track {

	@JsonProperty("latitude")
	public double latitude;

	@JsonProperty("longitude")
	public double longitude;

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
