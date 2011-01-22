package net.sardynka.marmur.abd.dape.estimator.interfaces;

public interface IDataCollector {
	/**
	 * 
	 * @param host - nazwa hosta z którego pochodzą statystyki
	 * @param KBwritten - ilość danych wysłanych do macierzy [KB]
	 * @param KBred - ilość danych odczytanych z macierzy [KB]
	 */
	public void pushUpdateData(String host, long KBwritten, long KBred);
}
