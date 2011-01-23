package net.sardynka.marmur.abd.dape.estimator.implementation;

import java.util.HashMap;
import java.util.Map;

import net.sardynka.marmur.abd.dape.estimator.implementation.Estimator.MaxSppedDetails;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EstimatorCoreFunctionsTest {
	
	public Estimator estimator;
	@Before
	public void before(){
		estimator = new Estimator(1, 1, 1);
	}
	
	
	@Test
	public void testSpeedSumation(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		readSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		readSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));
		
		//When
		long speed = estimator.getUsedCapacity(readSpeeds);
		
		//Then
		Assert.assertEquals(3*1024, speed);
	}


	@Test
	public void testMaxSpeedEstimationWithNewHost1(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		readSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		readSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		writeSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(10*1024, readSpeeds, writeSpeeds, "Host 3");

		
		//Then
		Assert.assertEquals(4*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(4*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(6*1024, sppedDetails.speedForOtherHosts);
	}



	@Test
	public void testMaxSpeedEstimationWithNewHost2(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(1 * 1024 /* 2MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(10*1024, readSpeeds, writeSpeeds, "Host 3");

		
		//Then
		Assert.assertEquals(9*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(9*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(1*1024, sppedDetails.speedForOtherHosts);
	}


	@Test
	public void testMaxSpeedEstimationWithNewHost3(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		readSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		readSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		writeSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(9*1024, readSpeeds, writeSpeeds, "Host 3");

		
		//Then
		Assert.assertEquals(3*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(3*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(6*1024, sppedDetails.speedForOtherHosts);
	}


	
	
	@Test
	public void testMaxSpeedEstimationWithNewHost4(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		readSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		readSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		writeSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(6*1024, readSpeeds, writeSpeeds, "Host 3");

		
		//Then
		Assert.assertEquals(2*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(2*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(4*1024, sppedDetails.speedForOtherHosts);
	}
	
	
	
	
	
	
	
	
	
	
	@Test
	public void testMaxSpeedEstimationWithExistingHost1(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		writeSpeeds.put("Host 2", new Long(2 * 1024 /* 2MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(4*1024, readSpeeds, writeSpeeds, "Host 2");
		
		//Then
		Assert.assertEquals(1*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(2*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(2*1024, sppedDetails.speedForOtherHosts);
	}

	@Test
	public void testMaxSpeedEstimationWithExistingHost2(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		writeSpeeds.put("Host 2", new Long(2 * 1024 /* 2MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(10*1024, readSpeeds, writeSpeeds, "Host 2");
		
		//Then
		Assert.assertEquals(6*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(8*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(2*1024, sppedDetails.speedForOtherHosts);
	}


	@Test
	public void testMaxSpeedEstimationWithExistingHost3(){
		//Given
		Map<String,Long> readSpeeds = new HashMap<String, Long>();

		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(2 * 1024 /* 2MBs */));
		writeSpeeds.put("Host 2", new Long(3 * 1024 /* 2MBs */));

		//When
		MaxSppedDetails sppedDetails = estimator.getMaximalSpeedForHost(6*1024, readSpeeds, writeSpeeds, "Host 2");
		
		//Then
		Assert.assertEquals(2*1024, sppedDetails.maxSpeedForConnection);
		Assert.assertEquals(4*1024, sppedDetails.maxSpeedForHost);
		Assert.assertEquals(2*1024, sppedDetails.speedForOtherHosts);
	}


}


