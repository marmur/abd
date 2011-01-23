package net.sardynka.marmur.abd.dape.estimator.implementation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sardynka.marmur.abd.dape.estimator.interfaces.IDataCollector;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IEstimator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class WriteEstimationsTest {
	
	
	
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
	
	
	
	
	public IDataCollector prepareDataMock1(){
		IDataCollector dataCollector = mock(IDataCollector.class);
		when(dataCollector.getUsedCacheSize()).thenReturn(new Long(10*1024 /* 10mB */));

		Map<String,Long> readSpeeds = new HashMap<String, Long>();
		readSpeeds.put("Host 1", new Long(0 /* 0MBs */));
		readSpeeds.put("Host 2", new Long(0 /* 0MBs */));
		when(dataCollector.getReadSpeedPerHost()).thenReturn(readSpeeds);
		
		
		Map<String,Long> writeSpeeds = new HashMap<String, Long>();
		writeSpeeds.put("Host 1", new Long(1 * 1024 /* 1MBs */));
		writeSpeeds.put("Host 2", new Long(1 * 1024 /* 1MBs */));
		when(dataCollector.getWriteSpeedPerHost()).thenReturn(writeSpeeds);

		return dataCollector;
	}
	
	
	
	
	
	
	
	
	@Test
	public void TestCacheWriteFromNewHost(){
		//Given
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 1Mbs r-s = 0Mbs
		 * Host 2 w-s = 1Mbs r-s = 0Mbs
		 * 
		 *  estimate 40MB write time for host 3 -> 5s (5000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1();
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long writeTime = estimator.getWriteEstimation("Host 3", 40 * 1024);
		
		//Then
		Assert.assertEquals(5000, writeTime);
	}
	
	@Test 
	public void TestCacheAndHddWriteFromNewHost(){
		//Given
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 1Mbs r-s = 0Mbs
		 * Host 2 w-s = 1Mbs r-s = 0Mbs
		 * 
		 *  estimate 90MB write time for host 3 :
		 *  	cache 72MB ->  9s ( 9 000ms)
		 *  	hdd   18MB -> 54s (54 000ms)
		 *  -------------------------------------
		 *  					  (63 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1();
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long writeTime = estimator.getWriteEstimation("Host 3", 90 * 1024);
		
		//Then
		Assert.assertEquals(63000, writeTime);
	}
	
	
	
	@Test
	public void TestCacheWriteFromActiveHost(){
		//Given
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 1Mbs r-s = 0Mbs
		 * Host 2 w-s = 1Mbs r-s = 0Mbs
		 * 
		 *  estimate 40MB write time for host 2 -> 5s (5000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1();
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long writeTime = estimator.getWriteEstimation("Host 2", 40 * 1024);
		
		//Then
		Assert.assertEquals(5000, writeTime);
	}
	
	
	@Test 
	public void TestCacheAndHddWriteFromActiveHost(){
		//Given
		/**
		 * Free cache: 90MB
		 * Max speed: 10MBs
		 * Max HDDs speed: 1MBs
		 * Host 1 w-s = 1MBs r-s = 0Mbs
		 * Host 2 w-s = 1MBs r-s = 0Mbs
		 * 
		 *  estimate 90MB write time for host 2 :
		 *  	cache 72MB ->  9s ( 9 000ms)
		 *  	hdd   18MB -> 72s (72 000ms)
		 *  -------------------------------------
		 *  					  (81 000ms)
		 */
		
		IDataCollector dataCollector = prepareDataMock1();
		Estimator estimator = prepareEstimator(dataCollector);
		
		//When
		long writeTime = estimator.getWriteEstimation("Host 2", 90 * 1024);
		
		//Then
		Assert.assertEquals(81000, writeTime);
	}

}
