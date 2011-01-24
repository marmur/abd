package net.sardynka.marmur.abd.dape.estimator.implementation;

import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;



public class DataColectorTest {
	private DataCollector dataCollector;
	
	@Before
	public void prepareDataCollector(){
		dataCollector = new DataCollector(1 * 1024 /* 1 MBs */, 100, 0.2);
	}
	

	
	
	
	@Test
	public void testInitialDataPush(){
		//given
		dataCollector.pushUpdateData("Host 1", 10, 20);
		
		//when
		Map<String,Long> writeSpeed = dataCollector.getWriteSpeedPerHost();		
		Map<String,Long> readSpeed  = dataCollector.getReadSpeedPerHost();
		
		//then
		assertNotNull(writeSpeed);
		assertNotNull(readSpeed);
		
		assertEquals(0,writeSpeed.size());
		assertEquals(0,readSpeed.size());
		
	}
	
	
	
	@Test
	public void testDataPush(){
		//given
		dataCollector.pushUpdateData("Host 1", 10, 20, 0);
		dataCollector.pushUpdateData("Host 1", 100, 200, 1000);
		
		//when
		Map<String,Long> writeSpeed = dataCollector.getWriteSpeedPerHost();		
		Map<String,Long> readSpeed  = dataCollector.getReadSpeedPerHost();
		
		//then
		assertNotNull(writeSpeed);
		assertNotNull(readSpeed);
		
		assertEquals(1,writeSpeed.size());
		assertEquals(1,readSpeed.size());
		
		assertEquals(new Long(80), writeSpeed.get("Host 1"));
		assertEquals(new Long(160),  readSpeed.get("Host 1"));
		
	}
	
	@Test
	public void testDataPush2(){
		//given
		dataCollector.pushUpdateData("Host 1", 10, 20, 0);
		dataCollector.pushUpdateData("Host 1", 100, 200, 2000);
		
		//when
		Map<String,Long> writeSpeed = dataCollector.getWriteSpeedPerHost();		
		Map<String,Long> readSpeed  = dataCollector.getReadSpeedPerHost();
		
		//then
		assertNotNull(writeSpeed);
		assertNotNull(readSpeed);
		
		assertEquals(1,writeSpeed.size());
		assertEquals(1,readSpeed.size());
		
		assertEquals(new Long(40), writeSpeed.get("Host 1"));
		assertEquals(new Long(80),  readSpeed.get("Host 1"));
		
	}

	
	@Test
	public void testDataPush3(){
		//given
		dataCollector.pushUpdateData("Host 1", 10, 20, 0);
		dataCollector.pushUpdateData("Host 1", 100, 200, 500);
		
		//when
		Map<String,Long> writeSpeed = dataCollector.getWriteSpeedPerHost();		
		Map<String,Long> readSpeed  = dataCollector.getReadSpeedPerHost();
		
		//then
		assertNotNull(writeSpeed);
		assertNotNull(readSpeed);
		
		assertEquals(1,writeSpeed.size());
		assertEquals(1,readSpeed.size());
		
		assertEquals(new Long(160), writeSpeed.get("Host 1"));
		assertEquals(new Long(320),  readSpeed.get("Host 1"));
		
	}

	
	
	@Test
	public void test2DataPushes(){
		//given
		dataCollector.pushUpdateData("Host 1", 10, 20, 0);
		dataCollector.pushUpdateData("Host 1", 100, 200, 1000);
		dataCollector.pushUpdateData("Host 1", 100, 200, 2000);
		
		//when
		Map<String,Long> writeSpeed = dataCollector.getWriteSpeedPerHost();		
		Map<String,Long> readSpeed  = dataCollector.getReadSpeedPerHost();
		
		//then
		assertNotNull(writeSpeed);
		assertNotNull(readSpeed);
		
		assertEquals(1,writeSpeed.size());
		assertEquals(1,readSpeed.size());
		
		assertEquals(new Long(96), writeSpeed.get("Host 1"));
		assertEquals(new Long(192),  readSpeed.get("Host 1"));
		
	}
}
