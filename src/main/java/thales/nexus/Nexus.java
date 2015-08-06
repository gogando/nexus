package thales.nexus;

import java.util.HashMap;

import com.google.inject.AbstractModule;

public class Nexus extends AbstractModule {

	public static void main(String[] args) throws InterruptedException {
		SBS1Connector connector = new SBS1Connector(new HashMap<String, Track>());
		connector.start();
		Thread.sleep(10000000);
	}

	protected void configure() {

	}

}
