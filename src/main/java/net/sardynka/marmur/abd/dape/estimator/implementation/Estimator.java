package net.sardynka.marmur.abd.dape.estimator.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import net.sardynka.marmur.abd.dape.estimator.interfaces.IDataCollector;
import net.sardynka.marmur.abd.dape.estimator.interfaces.IEstimator;

public class Estimator implements IEstimator {
	private static Logger log = Logger.getLogger(Estimator.class);
	
	private IDataCollector dataCollector;
	private long arrayMaxSpeed;
	private long arrayHddsMaxSpeed;
	private long arrayCacheSize;
	
	


	
	long getUsedCapacity(Map<String, Long> rwSpeed){
		long currentSpeed = 0;
		
		for (Entry<String, Long> entry :  rwSpeed.entrySet()){
			currentSpeed += entry.getValue();
		}
		
		return currentSpeed;
	}
	
	
	class MaxSppedDetails{
		long maxSpeedForConnection;
		long maxSpeedForHost;
		long speedForOtherHosts;
		int hostCount;
	}
	
	MaxSppedDetails getMaximalSpeedForHost(long referenceSpeed, Map<String, Long> readSpeed, Map<String, Long> writeSpeed, String hostname){
		MaxSppedDetails speedDetails = new MaxSppedDetails();
		
		Map<String, Long> ioUsage = new HashMap<String, Long>(readSpeed);
		
		for (Entry<String, Long> entry :  writeSpeed.entrySet()){
			Long hostIOUsage = ioUsage.get(entry.getKey());
			if (hostIOUsage == null){
				ioUsage.put(entry.getKey(), entry.getValue());
			}else{
				ioUsage.put(entry.getKey(),entry.getValue() + hostIOUsage);
			}
		}
		
		Long hostIOUsage =  ioUsage.get(hostname);			
		speedDetails.hostCount = ioUsage.size();
		if (hostIOUsage == null){
			speedDetails.hostCount += 1;
		}
		
		long usedCapacity = getUsedCapacity(ioUsage);
		if (usedCapacity > referenceSpeed) usedCapacity = referenceSpeed;
		log.debug("Used capacity: " + usedCapacity + " kB/s");
		long unusedCapacity = referenceSpeed - usedCapacity;
		if (unusedCapacity < 0) unusedCapacity =0;
		log.debug("Unused capacity: " + unusedCapacity + " kB/s");
		long idealSpeed = (referenceSpeed / speedDetails.hostCount) ;
		log.debug("Capacity share for one host: " + idealSpeed + " kB/s");

		
		if (hostIOUsage == null){
			/**
			 * No other process is using disk array resources from selected host
			 */
			if (idealSpeed < unusedCapacity){
				speedDetails.maxSpeedForConnection = unusedCapacity;
				speedDetails.speedForOtherHosts = usedCapacity;
			}else{
				speedDetails.maxSpeedForConnection = idealSpeed;
				speedDetails.speedForOtherHosts = referenceSpeed - idealSpeed;
			}
			speedDetails.maxSpeedForHost = speedDetails.maxSpeedForConnection;
		}else{
			if (hostIOUsage > idealSpeed) hostIOUsage = idealSpeed;
			
			long otherHostsSpeed = usedCapacity - hostIOUsage;
			log.debug("Capacity used by other hosts " + otherHostsSpeed + " kB/s");
			speedDetails.speedForOtherHosts = otherHostsSpeed;
			
			long capacityLeftForHost = referenceSpeed - otherHostsSpeed;
			log.debug("Capacity that can be used by this host: " + capacityLeftForHost + " kB/s");

			if (idealSpeed < capacityLeftForHost){
				speedDetails.maxSpeedForHost = capacityLeftForHost;
			}else{
				speedDetails.maxSpeedForHost = idealSpeed;
			}
						
			if (hostIOUsage < capacityLeftForHost/2){
				speedDetails.maxSpeedForConnection = speedDetails.maxSpeedForHost - hostIOUsage;
			}else{
				speedDetails.maxSpeedForConnection = speedDetails.maxSpeedForHost /2;
			}
		}
		
		log.debug("Returned values: [connection: " + speedDetails.maxSpeedForConnection
								+" ][host: " + speedDetails.maxSpeedForHost
								+" ][other: " + speedDetails.speedForOtherHosts + "]");
		return speedDetails;
	}
	
	

	public long getReadEstimation(String host, long fileSize) {
		Map<String,Long> writeSpeedPerHost = dataCollector.getWriteSpeedPerHost();
		Map<String,Long> readSpeedPerHost = dataCollector.getReadSpeedPerHost();
		
		MaxSppedDetails speedDetails = getMaximalSpeedForHost(arrayHddsMaxSpeed, readSpeedPerHost, writeSpeedPerHost, host);
		
		return (fileSize / speedDetails.maxSpeedForConnection) * 1000;
	}
	
	
	

	public long getWriteEstimation(String host, long fileSize) {
		long freeCache = arrayCacheSize - dataCollector.getUsedCacheSize();
		Map<String,Long> writeSpeedPerHost = dataCollector.getWriteSpeedPerHost();
		Map<String,Long> readSpeedPerHost = dataCollector.getReadSpeedPerHost();
		
		MaxSppedDetails speedDetails = getMaximalSpeedForHost(arrayMaxSpeed, readSpeedPerHost, writeSpeedPerHost, host); 
		
		
		
		long msToFullyConsumeArrayCache = (freeCache / arrayMaxSpeed) * 1000;		                                   
		log.debug("Time to fully consume arrays cache: " + msToFullyConsumeArrayCache + " ms");
		
		long ammoutOfDataWrittenToCache = (speedDetails.maxSpeedForConnection * msToFullyConsumeArrayCache) / 1000;
		log.debug("Data that will be written to cache before its depleated: " + ammoutOfDataWrittenToCache + "kB");
		
		if (ammoutOfDataWrittenToCache > fileSize){
			return (fileSize / speedDetails.maxSpeedForConnection) * 1000;
		}else{
			fileSize -= ammoutOfDataWrittenToCache;
			MaxSppedDetails speedForHddWrite = getMaximalSpeedForHost(arrayHddsMaxSpeed, readSpeedPerHost, writeSpeedPerHost, host);
			return msToFullyConsumeArrayCache + ((fileSize / speedForHddWrite.maxSpeedForConnection) * 1000);
		}
	}

	public IDataCollector getDataCollector() {
		return dataCollector;
	}

	
	public Estimator(long arrayMaxSpeed, long arrayHddsMaxSpeed, long arrayCacheSize){
		this(arrayMaxSpeed, arrayHddsMaxSpeed, arrayCacheSize, new DataCollector(arrayHddsMaxSpeed, 1000, 0.2));
	}

	public Estimator(long arrayMaxSpeed, long arrayHddsMaxSpeed, long arrayCacheSize, IDataCollector dataCollector){
		this.arrayMaxSpeed = arrayMaxSpeed;
		this.arrayHddsMaxSpeed = arrayHddsMaxSpeed;
		this.arrayCacheSize = arrayCacheSize;
		this.dataCollector = dataCollector;
	}
}
