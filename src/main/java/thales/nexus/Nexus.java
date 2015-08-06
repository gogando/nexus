package thales.nexus;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.glassfish.grizzly.http.server.HttpServer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;

public class Nexus {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException,
			IllegalArgumentException, NullPointerException, IOException {
		Injector injector = Guice.createInjector(new NexusModule());
		ResourceConfig config = new PackagesResourceConfig("thales.nexus.handlers", "thales.nexus.connectors");
		config.getContainerRequestFilters().add(CorsSupportFilter.class);
		GuiceComponentProviderFactory iocComponentProviderFactory = new GuiceComponentProviderFactory(config, injector);
		HttpServer server = GrizzlyServerFactory.createHttpServer("http://0.0.0.0:80", config, iocComponentProviderFactory);
		
		server.start();

		System.out.println("Press ENTER to exit...");

		int key;
		do {
			key = System.in.read();
		} while (key != KeyEvent.VK_ENTER);

		System.out.println("Shutting down...");
		server.stop();

		// while (true) {
		//
		// trackStore.forEach((trackId, track) -> {
		// if (track instanceof AirTrack) {
		// AirTrack air = (AirTrack) track;
		// System.out.println(air.lat);
		// System.out.println(air.lon);
		// System.out.println(air.icao);
		// System.out.println(air.onGround);
		// }
		//
		// });
		//
		// try {
		// Thread.sleep(1000);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }

	}

}
