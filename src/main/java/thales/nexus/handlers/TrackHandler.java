package thales.nexus.handlers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import thales.nexus.TrackStore;
import thales.nexus.Tracks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Path("/tracks")
public class TrackHandler {

	private final TrackStore trackStore;

	@Inject
	public TrackHandler(TrackStore trackStore) {
		this.trackStore = trackStore;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Tracks allTracks() throws JsonProcessingException {
		Tracks tracks = new Tracks();
		tracks.setTracks(trackStore);
		return tracks;
	}

}
