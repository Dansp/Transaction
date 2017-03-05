package dansp.com.transaction;

/**
 * Utility class in Singleton to dispatcher listeners
 * @author Daniel S. Pereira
 * @since 05-12-2014
 */
public class Dispatcher {
	
	private static Dispatcher instance;

	private ProgressUpdate progresBarUpdate;
	
	/**
	 * Static method to get the instance of object created
	 * @return instance of Dispatcher
	 */
	public static Dispatcher getInstance(){
		if(instance == null){
			instance = new Dispatcher();
		}
		return instance;
	}

	/**
	 * @param progressUpdate from interface ProgressUpdate
	 */
	public void setProgressBar(ProgressUpdate progressUpdate){
		this.progresBarUpdate = progressUpdate;
	}

	/**
	 * Dispatcher the listener of ProgressBarUpdate
	 * @param actualSize actual size of progress bar
	 */
    public void dispatchListenerProgressBarUpdate(int actualSize){
        if(progresBarUpdate != null)
        progresBarUpdate.onUpdateProgressBar(actualSize);
    }
}