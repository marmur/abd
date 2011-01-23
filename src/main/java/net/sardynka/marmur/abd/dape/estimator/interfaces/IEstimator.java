package net.sardynka.marmur.abd.dape.estimator.interfaces;

public interface IEstimator {

	/**
	 * 
	 * @param host - nazwa hosta dla którego wykonywana jest estymacja
	 * @param fileSize - rozmiar pliku do odczytu [KB]
	 * @return - czas odczytu pliku [ms]
	 */
	public long getReadEstimation(String host, long fileSize);
	
	
	/**
	 * 
	 * @param host - nazwa hosta dla którego wykonywana jest estymacja
	 * @param fileSize - rozmiar pliku do zapis [KB]
	 * @return - czas zapisu pliku (do urządzenia, czyli m.in. do cache'a macierzy) [ms]
	 */
	public long getWriteEstimation(String host, long fileSize);

	public IDataCollector getDataCollector();
}
