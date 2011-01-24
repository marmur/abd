package net.sardynka.marmur.abd.dape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import net.sardynka.marmur.abd.dape.estimator.implementation.EstimatorBuilder;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IEstimator;

@Path("/e")
// @Produces("application/json")
public class StorageServiceRest {
	IEstimator ie;

	public StorageServiceRest() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(StorageServiceRest.class.getClassLoader().getResourceAsStream("configuration.properties"));
		ie = new EstimatorBuilder().setProperties(props).build();
	}

	@GET
	@Path("/write/host/{host}/size/{fileSize}")
	public Long getWriteEstimation(@PathParam("host") String host,
			@PathParam("fileSize") long filesize) {
		return new Long(ie.getWriteEstimation(host, filesize));
	}

	@GET
	@Path("/read/host/{host}/size/{fileSize}")
	public Long getReadEstimation(@PathParam("host") String host,
			@PathParam("fileSize") long filesize) {
		return new Long(ie.getReadEstimation(host, filesize));
	}

	@GET
	@Path("/push/host/{host}/written/{written}/read/{read}")
	public Response pushUpdateData(@PathParam("host") String host,
			@PathParam("written") long KBwritten, @PathParam("read") long KBred) {
		ie.getDataCollector().pushUpdateData(host, KBwritten, KBred);
		return Response.ok(host).build();
	}
}
