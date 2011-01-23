package net.sardynka.marmur.abd.dape.estimator.implementation;

import static org.junit.Assert.*;
import org.junit.Test;


public class DataCollectorSimulationTest {

	
	
	@Test
	public void testDataCollectorSimulation() throws InterruptedException{
		//given
		DataCollector dataCollector = new DataCollector(5 * 1024,200,0);
		Thread.sleep(10);		
		dataCollector.pushUpdateData("1", 10*1024, 0,0);
		dataCollector.pushUpdateData("1", 10*1024, 0,1000);
		
		//when
		Thread.sleep(200);
		long usedCacheSize = dataCollector.getUsedCacheSize();
		
		//then
		assertEquals(1024, usedCacheSize);
	}
	
	
	@Test
	public void testDataCollectorSimulation2() throws InterruptedException{
		//given
		DataCollector dataCollector = new DataCollector(5 * 1024,200,0);
		Thread.sleep(10);		
		dataCollector.pushUpdateData("1", 10*1024, 0,0);
		dataCollector.pushUpdateData("1", 10*1024, 0,1000);
		Thread.sleep(200);
		dataCollector.pushUpdateData("1", 20*1024, 0,2000);
		
		//when
		Thread.sleep(200);
		long usedCacheSize = dataCollector.getUsedCacheSize();
		
		//then
		assertEquals(4 * 1024, usedCacheSize);
	}

	
	@Test
	public void testDataCollectorSimulation3() throws InterruptedException{
		//given
		DataCollector dataCollector = new DataCollector(5 * 1024,200,0);
		Thread.sleep(10);		
		dataCollector.pushUpdateData("1", 10*1024, 0,0);
		dataCollector.pushUpdateData("1", 10*1024, 0,1000);
		Thread.sleep(200);
		dataCollector.pushUpdateData("1", 0, 0,2000);
		
		//when
		Thread.sleep(200);
		long usedCacheSize = dataCollector.getUsedCacheSize();
		
		//then
		assertEquals(0 * 1024, usedCacheSize);
	}

	
	@Test
	public void testDataCollectorSimulation4() throws InterruptedException{
		//given
		DataCollector dataCollector = new DataCollector(5 * 1024,200,0);
		Thread.sleep(10);		
		dataCollector.pushUpdateData("1", 10*1024, 0,0);
		dataCollector.pushUpdateData("1", 10*1024, 5*1024,1000);
		
		//when
		Thread.sleep(200);
		long usedCacheSize = dataCollector.getUsedCacheSize();
		
		//then
		assertEquals(2 * 1024, usedCacheSize);
	}

	
	@Test
	public void testDataCollectorSimulation5() throws InterruptedException{
		//given
		DataCollector dataCollector = new DataCollector(5 * 1024,200,0);
		Thread.sleep(10);		
		dataCollector.pushUpdateData("1", 10*1024, 0,0);
		dataCollector.pushUpdateData("1", 10*1024, 5*1024,1000);
		Thread.sleep(200);
		dataCollector.pushUpdateData("1", 0, 5*1024,2000);
		
		//when
		Thread.sleep(200);
		long usedCacheSize = dataCollector.getUsedCacheSize();
		
		//then
		assertEquals(2 * 1024, usedCacheSize);
	}

}
