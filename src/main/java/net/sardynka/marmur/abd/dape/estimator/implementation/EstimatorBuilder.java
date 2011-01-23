package net.sardynka.marmur.abd.dape.estimator.implementation;

import java.util.Properties;

import net.sardynka.marmur.abd.dape.estimator.interfaces.IDataCollector;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IEstimator;

public class EstimatorBuilder {
	/**
	 * Maximal speed of array (in kB/s)
	 * Property name: array.speed.max  
	 */
	private long arrayMaxSpeed = 0;
	private boolean isArrayMaxSpeedSet = false;
	
	/**
	 * Maximal speed of array after cache is completely used (in kB/s)
	 * Property name: array.speed.hdd
	 */
	private long arrayHddsMaxSpeed = 0;
	private boolean isArrayHddsMaxSpeedSet = false;
	
	/**
	 * Size of array cache (in KB)
	 * Property name: array.cache.size
	 */
	private long arrayCacheSize = 0;
	private boolean isArrayCacheSizeSet = false;
	
	
	private IDataCollector dataCollector = null;
	
	public EstimatorBuilder setProperties(Properties arrayConfiguration){
		String arrayMaxSpeed = arrayConfiguration.getProperty("array.speed.max");
		if (arrayMaxSpeed != null){
			this.setArrayMaxSpeed(Long.parseLong(arrayMaxSpeed));
		}
				
		String arrayHddsMaxSpeed = arrayConfiguration.getProperty("array.speed.hdd");
		if (arrayHddsMaxSpeed != null){
			this.setArrayHddsMaxSpeed(Long.parseLong(arrayHddsMaxSpeed));
		}
		
		String arrayCacheSize = arrayConfiguration.getProperty("array.cache.size");
		if (arrayCacheSize != null){
			this.setArrayCacheSize(Long.parseLong(arrayCacheSize));
		}
		
		return this;
	}
	
	public EstimatorBuilder setArrayMaxSpeed(long arrayMaxSpeed){
		this.arrayMaxSpeed = arrayMaxSpeed;
		this.isArrayMaxSpeedSet = true;
		
		return this;
	}
	
	public EstimatorBuilder setArrayHddsMaxSpeed(long arrayHddsMaxSpeed){
		this.arrayHddsMaxSpeed = arrayHddsMaxSpeed;
		this.isArrayHddsMaxSpeedSet = true;
		
		return this;
	}
	
	public EstimatorBuilder setArrayCacheSize(long arrayCacheSize){
		this.arrayCacheSize = arrayCacheSize;
		this.isArrayCacheSizeSet = true;
		
		return this;
	}
	
	public EstimatorBuilder setDataCollector(IDataCollector dataCollector){
		this.dataCollector = dataCollector;
		
		return this;
	}
	
	public IEstimator build(){
		if (isArrayCacheSizeSet == false){
			throw new RuntimeException("Array Cache Size not set");
		}
		
		if (isArrayHddsMaxSpeedSet == false){
			throw new RuntimeException("Array Max HDDs Speed not set");
		}
		
		if (isArrayMaxSpeedSet == false){
			throw new RuntimeException("Array Max Speed not set");
		}
		
		if (dataCollector != null){
			return new Estimator(arrayMaxSpeed, arrayHddsMaxSpeed, arrayCacheSize, dataCollector);
		}else{			
			return new Estimator(arrayMaxSpeed, arrayHddsMaxSpeed, arrayCacheSize);
		}
	}
}
