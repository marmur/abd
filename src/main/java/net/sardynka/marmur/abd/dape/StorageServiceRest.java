package net.sardynka.marmur.abd.dape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.sardynka.marmur.abd.dape.estimator.implementation.EstimatorBuilder;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IEstimator;

@Path("/e")
@Produces("application/json")
public class StorageServiceRest {
	IEstimator ie;
	
	public StorageServiceRest() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("res/configuration.properties")));
		ie = new EstimatorBuilder().setProperties(props).build();
	}
	
	@GET
	@Path("/write/host/{host}/size/{fileSize}")
	public Long getWriteEstimation(@PathParam("host") String host, @PathParam("fileSize") String filesize) {
		System.out.println(host);
		System.out.println(filesize);
		System.out.println("alamakota");
		//return new Long(1);
		//return Long.parseLong(filesize);
		return new Long(ie.getWriteEstimation(host, Long.parseLong(filesize)));
	}

	@GET
	@Path("/read/host/{host}/size/{fileSize}")
	public Long getReadEstimation(@PathParam("host") String host, @PathParam("fileSize") String filesize) {
		return new Long(ie.getReadEstimation(host, Long.parseLong(filesize)));
	}
}
