package net.sardynka.marmur.abd.dape;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JSONProvider;

public class App {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		String address = null;
		if (args.length == 0) {
			address = "http://127.0.0.1:63563/";
		} else if (args.length == 1) {
			address = args[0];
		} else {
			System.out.println("Wrong number of parameters");
			System.out.println();
			System.out.println("Usage: java App.java [service address]");
			System.out
					.println("  service address - default: http://127.0.0.1:63563");
			System.exit(13);
		}
		StorageServiceRest storageServiceRest = new StorageServiceRest();

		JAXRSServerFactoryBean restServer = new JAXRSServerFactoryBean();
		restServer.setResourceClasses(String.class);
		restServer.setServiceBeans(storageServiceRest);
		restServer.setAddress(address);
		JSONProvider provider = new JSONProvider();
		provider.setDropRootElement(true);
		restServer.setProvider(provider);
		restServer.create();
	}
}
