package net.sardynka.marmur.abd.dape.estimator.implementation;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


import net.sardynka.marmur.abd.dape.estimator.interfaces.IDataCollector;

public class DataCollector implements IDataCollector {
	private static final Logger log = Logger.getLogger(DataCollector.class);
	private long estimatedCacheUsage = 0;
	private long arrayMaxHddSpeed;
	private double archiveDataWeight;
	private long msBetweenRefresh;
	
	private class HostDescription{
		long writeSpeed = 0;
		long readSpeed = 0;
		long latestData = 0;
	}
	
	
	private Map<String, HostDescription> ioOperationsDetails;
	
	
	
	public void pushUpdateData(String host, long KBwritten, long KBred) {
		pushUpdateData(host, KBwritten, KBred, Calendar.getInstance().getTimeInMillis());
	}
	
	void pushUpdateData(String host, long KBwritten, long KBred, long now) {
		
		
		HostDescription description = ioOperationsDetails.get(host);
		if (description == null){
			description = new HostDescription();
			description.latestData = now;
			description.readSpeed = 0;
			description.writeSpeed = 0;
			
			ioOperationsDetails.put(host, description);
		}else{
			synchronized (description) {
				long timeDelta = now - description.latestData;
				double multiplier = (double)1000/(double)timeDelta;
				
				description.writeSpeed = Math.round(((description.writeSpeed * archiveDataWeight) + ( (1-archiveDataWeight) * KBwritten *multiplier)));
				description.readSpeed =  Math.round(((description.readSpeed  * archiveDataWeight) + ( (1-archiveDataWeight) * KBred     *multiplier)));
				description.latestData = now;
				
				ioOperationsDetails.put(host, description);				
			}			
		}
	}
	
	
	
	public long getUsedCacheSize() {
		return estimatedCacheUsage;
	}

	public Map<String, Long> getWriteSpeedPerHost() {
		Map<String, Long> writeSpeeds = new HashMap<String, Long>();
		
		for (Entry<String, HostDescription> e : ioOperationsDetails.entrySet()){
			if (e.getValue().writeSpeed > 0){
				writeSpeeds.put(e.getKey(), e.getValue().writeSpeed);
			}
		}
		return writeSpeeds;
	}

	public Map<String, Long> getReadSpeedPerHost() {
		Map<String, Long> readSpeeds = new HashMap<String, Long>();
		for (Entry<String, HostDescription> e : ioOperationsDetails.entrySet()){
			if (e.getValue().readSpeed > 0){
				readSpeeds.put(e.getKey(), e.getValue().readSpeed);
			}
		}
		return readSpeeds;		
	}

	
	public DataCollector(long _arrayMaxHddSpeed, long _msBetweenRefresh, double archiveDataWeight){
		ioOperationsDetails = new HashMap<String, DataCollector.HostDescription>();
		this.archiveDataWeight = archiveDataWeight;
		this.arrayMaxHddSpeed = _arrayMaxHddSpeed;
		this.msBetweenRefresh = _msBetweenRefresh;
		
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				long writeSpeed = 0;
				long readSpeed = 0;
				
				for (Entry<String, HostDescription> e : ioOperationsDetails.entrySet()){
					writeSpeed += e.getValue().writeSpeed;
					readSpeed += e.getValue().readSpeed;
				}
				
				estimatedCacheUsage += writeSpeed * msBetweenRefresh / 1000;
				
				long hddWriteSpeed = (arrayMaxHddSpeed - readSpeed);
				if (hddWriteSpeed>0){
					estimatedCacheUsage -= hddWriteSpeed * msBetweenRefresh / 1000; 
				}
			}
		}, 0, msBetweenRefresh);
	}
}
