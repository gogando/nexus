package thales.nexus;

import java.util.HashMap;

import com.google.inject.Singleton;

@Singleton
public class TrackStore extends HashMap<String, Track> {

	private static final long serialVersionUID = 1L;

	public TrackStore() {
	}

}
