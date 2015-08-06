package thales.nexus;

import thales.nexus.connectors.SBS1Connector;

import com.google.inject.AbstractModule;

public class NexusModule extends AbstractModule {

	private final TrackStore trackStore = new TrackStore();

	@Override
	protected void configure() {
		bind(TrackStore.class).toInstance(trackStore);
		bind(Connector.class).to(SBS1Connector.class).asEagerSingleton();;
	}

}
