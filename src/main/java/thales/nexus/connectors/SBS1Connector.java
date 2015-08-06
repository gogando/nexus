package thales.nexus.connectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.inject.Named;

import thales.nexus.AirTrack;
import thales.nexus.Connector;
import thales.nexus.TrackStore;
import net.staniscia.sbs1.SBS1Observer;
import net.staniscia.sbs1.SBS1Parser;
import net.staniscia.sbs1.msg.AIR;
import net.staniscia.sbs1.msg.CLK;
import net.staniscia.sbs1.msg.ID;
import net.staniscia.sbs1.msg.MSG1;
import net.staniscia.sbs1.msg.MSG2;
import net.staniscia.sbs1.msg.MSG3;
import net.staniscia.sbs1.msg.MSG4;
import net.staniscia.sbs1.msg.MSG5;
import net.staniscia.sbs1.msg.MSG6;
import net.staniscia.sbs1.msg.MSG7;
import net.staniscia.sbs1.msg.MSG8;
import net.staniscia.sbs1.msg.SEL;
import net.staniscia.sbs1.msg.STA;
import net.staniscia.sbs1.parser.ParserFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Named("adsb")
public class SBS1Connector implements Connector, SBS1Observer {

	private final SBS1Parser parser = ParserFactory.getDefaultParser();

	private boolean running = false;

	private final TrackStore trackStore;

	@Inject
	public SBS1Connector(TrackStore trackStore) {
		this.trackStore = trackStore;
	}

	@Inject
	public void start() {
		this.running = true;
		parser.register(this);
		new Server().start();
	}

	public void stop() {
		this.running = false;
		parser.unRegister(this);
	}

	private class Server extends Thread {

		public void run() {
			try (ServerSocket server = new ServerSocket(30003)) {
				while (running) {
					new Client(server.accept()).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class Client extends Thread {

		private final Socket client;

		public Client(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			try (InputStream in = client.getInputStream()) {
				InputStreamReader streamReader = new InputStreamReader(in);
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				while (running) {
					String line = bufferedReader.readLine();
					parser.processIt(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void update(MSG1 message) {
		
	}

	public void update(MSG2 message) {
		
	}

	public void update(MSG3 message) {
		AirTrack track = (AirTrack) trackStore.getOrDefault(message.getHexIdent(), new AirTrack());
		track.latitude = Double.parseDouble(message.getLatitude());
		track.longitude = Double.parseDouble(message.getLongitude());
		track.altitude = Integer.parseInt(message.getAltitude());
		track.flightId = message.getFlightId();
		trackStore.put(message.getHexIdent(), track);
	}

	public void update(MSG4 message) {
		AirTrack track = (AirTrack) trackStore.getOrDefault(message.getHexIdent(), new AirTrack());
		track.flightId = message.getFlightId();
		track.icao = message.getAircraftId();
		trackStore.put(message.getHexIdent(), track);
	}

	public void update(MSG5 message) {
		AirTrack track = (AirTrack) trackStore.getOrDefault(message.getHexIdent(), new AirTrack());
		track.flightId = message.getFlightId();
		track.icao = message.getAircraftId();
		trackStore.put(message.getHexIdent(), track);
	}

	public void update(MSG6 message) {
		AirTrack track = (AirTrack) trackStore.getOrDefault(message.getHexIdent(), new AirTrack());
		track.icao = message.getAircraftId();
		track.squawk = message.getSquawk();
//		track.altitude = Integer.parseInt(message.getAltitude());
		trackStore.put(message.getHexIdent(), track);
	}

	public void update(MSG7 message) {
		AirTrack track = (AirTrack) trackStore.getOrDefault(message.getHexIdent(), new AirTrack());
		track.flightId = message.getFlightId();
		track.icao = message.getAircraftId();
		trackStore.put(message.getHexIdent(), track);
	}

	public void update(MSG8 message) {
		AirTrack track = (AirTrack) trackStore.getOrDefault(message.getHexIdent(), new AirTrack());
		track.flightId = message.getFlightId();
		track.icao = message.getAircraftId();
		track.onGround = message.isOnGround();
		trackStore.put(message.getHexIdent(), track);
	}

	public void update(ID message) {
		
	}

	public void update(SEL message) {
		
	}

	public void update(AIR message) {
		
	}

	public void update(STA message) {
		
	}

	public void update(CLK message) {
		
	}

}
