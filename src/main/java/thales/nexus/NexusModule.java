package thales.nexus;

import static com.google.inject.name.Names.named;
import thales.nexus.connectors.AISConnector;
import thales.nexus.connectors.SBS1Connector;

import com.google.inject.AbstractModule;

public class NexusModule extends AbstractModule {

	private final TrackStore trackStore = new TrackStore();

	@Override
	protected void configure() {
		bind(TrackStore.class).toInstance(trackStore);
		bind(Connector.class).annotatedWith(named("adsb")).to(SBS1Connector.class).asEagerSingleton();
		bind(Connector.class).annotatedWith(named("ais")).to(AISConnector.class).asEagerSingleton();
	}

}
