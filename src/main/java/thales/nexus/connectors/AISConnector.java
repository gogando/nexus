package thales.nexus.connectors;

import javax.inject.Named;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import thales.nexus.Connector;

@Singleton
@Named("ais")
public class AISConnector implements Connector {
	
	@Inject
	public void start() {

	}

	public void stop() {

	}

}
