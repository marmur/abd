package net.sardynka.marmur.abd.dape.estimator.implementation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sardynka.marmur.abd.dape.estimator.interfaces.IDataCollector;


public class ReadEstimationsTest {

	public Estimator prepareEstimator(IDataCollector dataCollector){
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Cache size = 100 MB
		 */
		Estimator estimator = new Estimator(10 * 1024 , 1 * 1024 , 100 * 1024, dataCollector);
		return estimator;
	}
	
	
	
	
	public IDataCollector prepareDataMock1(long usedCacheSize){
		IDataCollector dataCollector = mock(IDataCollector.class);
		when(dataCollector.getUsedCacheSize()).thenReturn(new Long(usedCacheSize*1024 ));

		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		readSpeeds.put("Host 1", new Long(0 /* 0MBs */));
		readSpeeds.put("Host 2", new Long(0 /* 0MBs */));
		when(dataCollector.getReadSpeedPerHost()).thenReturn(readSpeeds);
		
		
		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(512));
		writeSpeeds.put("Host 2", new Long(512));
		when(dataCollector.getWriteSpeedPerHost()).thenReturn(writeSpeeds);

		return dataCollector;
	}
	
	public IDataCollector prepareDataMock2(long usedCacheSize){
		IDataCollector dataCollector = mock(IDataCollector.class);
		when(dataCollector.getUsedCacheSize()).thenReturn(new Long(usedCacheSize*1024 ));

		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		when(dataCollector.getReadSpeedPerHost()).thenReturn(readSpeeds);
		
		
		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		when(dataCollector.getWriteSpeedPerHost()).thenReturn(writeSpeeds);

		return dataCollector;
	}
	
	
	
	@Test
	public void testReadFromNewHostWithFreeCache(){
		//Given
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 512KBs r-s = 0MBs
		 * Host 2 w-s = 512KBs r-s = 0MBs
		 * 
		 *  estimate 10MB read time for host 3 :
		 *  	hdd   10MB -> 30s (30 000ms)
		 *  -------------------------------------
		 *  					  (30 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1(10);
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long readTime = estimator.getReadEstimation("Host 3", 10 * 1024);
		
		//Then
		Assert.assertEquals(30000, readTime);
	}
	
	
	@Test
	public void testReadFromNewHost(){
		//Given
		/**
		 * Free cache: 0MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 512KBs r-s = 0KBs
		 * Host 2 w-s = 512KBs r-s = 0KBs
		 * 
		 *  estimate 10MB read time for host 3 :
		 *  	hdd   10MB -> 30s (30 000ms)
		 *  -------------------------------------
		 *  					  (30 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1(100);
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long readTime = estimator.getReadEstimation("Host 3", 10 * 1024);
		
		//Then
		Assert.assertEquals(30000, readTime);
		
	}
	
	
	@Test
	public void testReadFromExsistingHostWithFreeCache(){
		//Given
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 512KBs r-s = 0KBs
		 * Host 2 w-s = 512KBs r-s = 0KBs
		 * 
		 *  estimate 10MB read time for host 1 :
		 *  	hdd   10MB -> 40s (40 000ms)
		 *  -------------------------------------
		 *  					  (40 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1(10);
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long readTime = estimator.getReadEstimation("Host 1", 10 * 1024);
		
		//Then
		Assert.assertEquals(40000, readTime);		
	}
	
	@Test
	public void testReadFromExistingHost(){
		//Given
		/**
		 * Free cache: 0MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 512KBs r-s = 0KBs
		 * Host 2 w-s = 512KBs r-s = 0KBs
		 * 
		 *  estimate 10MB read time for host 1 :
		 *  	hdd   10MB -> 40s (40 000ms)
		 *  -------------------------------------
		 *  					  (40 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1(100);
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long readTime = estimator.getReadEstimation("Host 1", 10 * 1024);
		
		//Then
		Assert.assertEquals(40000, readTime);
	}
	
	@Test
	public void testReadFromOnlyHost(){
		//Given
		/**
		 * Free cache: 100MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * 
		 *  estimate 10MB read time for host 1 :
		 *  	hdd   10MB -> 10s (10 000ms)
		 *  -------------------------------------
		 *  					  (10 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock2(0);
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long readTime = estimator.getReadEstimation("Host 1", 10 * 1024);
		
		//Then
		Assert.assertEquals(10000, readTime);		
	}
	
	
	@Test
	public void testReadFromOnlyHostWithFullCache(){
		//Given
		/**
		 * Free cache: 0MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * 
		 *  estimate 10MB read time for host 1 :
		 *  	hdd   10MB -> 10s (10 000ms)
		 *  -------------------------------------
		 *  					  (10 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock2(100);
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long readTime = estimator.getReadEstimation("Host 1", 10 * 1024);
		
		//Then
		Assert.assertEquals(10000, readTime);				
	}
}
