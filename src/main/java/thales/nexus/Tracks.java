package thales.nexus;

import java.util.Collection;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tracks {

	private HashMap<String, Track> tracks = new HashMap<>();

	public void setTracks(HashMap<String, Track> trackStore) {
		this.tracks = trackStore;
	}

	@JsonProperty("tracks")
	public Collection<Track> getTracks() {
		return tracks.values();
	}

}
