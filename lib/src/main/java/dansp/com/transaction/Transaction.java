package dansp.com.transaction;

import android.content.Context;

/**
 * Abstract class that facilitates handling of threads in Android.
 * @author Daniel S. Pereira
 *
 */
abstract class Transaction {

	/**
	 * Context of activity on startTransaction in transactionManager
	 */
	private Context mContext;

	/**
	 * Execute the transaction in a separate thread.
	 */
	abstract boolean execute();

	/**
	 * Get the listener on cancel thread
	 */
	void onCancel(){}

	/**
	 * Method to update View From user after thread is finished.
	 */
	void updateView(boolean dataOk){}

	protected Context getmContext() {
		return mContext;
	}

	void setmContext(Context mContext) {
		if(this.mContext != null) {
			throw new IllegalAccessError("Cannot set a new stance in mContext");
		}
		this.mContext = mContext;
	}
}
