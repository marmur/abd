package net.sardynka.marmur.abd.dape;

import net.sardynka.marmur.abd.dape.estimator.implementation.EstimatorBuilder;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IDataCollector;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IEstimator;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EstimatorBuilderTest {
	
	
	//Expect RuntimeException to be thrown
	@Test 
	public void TestNoArrayMaxSpeed(){
		try {
			new EstimatorBuilder().setArrayCacheSize(10).setArrayHddsMaxSpeed(10).build();
			
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals("Array Max Speed not set",e.getMessage());
		}
	}
	
	
	@Test
	public void TestCorrectConfiguration(){
		IEstimator estimator = new EstimatorBuilder()
									.setArrayCacheSize(10)
									.setArrayHddsMaxSpeed(10)
									.setArrayMaxSpeed(10)
									.build();
		
		Assert.assertNotNull(estimator);	
		Assert.assertNotNull(estimator.getDataCollector());
	}

	//Expect RuntimeException to be thrown
	@Test 
	public void TestNoHddsMaxSpeed(){
		try {
			new EstimatorBuilder().setArrayCacheSize(10).setArrayMaxSpeed(10).build();
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals("Array Max HDDs Speed not set",e.getMessage());
		}
	}

	
	//Expect RuntimeException to be thrown
	@Test 
	public void TestNoCacheSize(){
		try {
			new EstimatorBuilder().setArrayHddsMaxSpeed(10).setArrayMaxSpeed(10).build();
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals("Array Cache Size not set",e.getMessage());
		}
	}
	
	
	@Test
	public void TestDataCollectorConfiguration(){
		//Given
		IDataCollector dataCollector = Mockito.mock(IDataCollector.class);
		
		EstimatorBuilder estimatorBuilder = new EstimatorBuilder()
													.setArrayCacheSize(10)
													.setArrayHddsMaxSpeed(10)
													.setArrayMaxSpeed(10);

		//When
		IEstimator estimator = estimatorBuilder.setDataCollector(dataCollector).build();
		
		//Then
		Assert.assertNotNull(estimator);
		Assert.assertSame(dataCollector, estimator.getDataCollector());		
	}	
}
