package dansp.com.transaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;

import android.util.Log;

/**
 * The TransactionTask is a class in AsyncTask to run in background and update view When it's is Finished.
 * @author Rafael Amizes.
 * @author Daniel S. Pereira
 */
public class TransactionTask extends AsyncTask<Void, Integer, Boolean> {

	public interface OnUpdateView {
		void onUpdateView(Transaction t, boolean ok);
	}

	private static final String TAG = "TransactionTask";
	private final Context context;
	private final Transaction transaction;
	private ProgressDialog progress;
	private String waitMessage;
    private boolean hasProgressBar;
    private FinishedTaskListener mFinishedTaskListener;
	private int currentOrientation;
	OnUpdateView updateListener;

	interface FinishedTaskListener {
        void onFinished(TransactionTask task);
    }


	/**
	 * @param context context
	 * @param transaction classe que implementa a interface Transaction.
	 * @param waitMessage message opcional de espera.
	 */
    TransactionTask(Context context, Transaction transaction, String waitMessage, boolean hasProgressBar) {
		this.context     = context;
		this.transaction = transaction;
		this.waitMessage = waitMessage;
        this.hasProgressBar = hasProgressBar;
		this.transaction.setmContext(context);
	}
	/**
	 * Without Message.
	 * @param context context
	 */
    TransactionTask(Context context, Transaction transaction) {
		this.context     = context;
		this.transaction = transaction;
		this.transaction.setmContext(context);
	}

    void setFinishedTaskListener(FinishedTaskListener mFinishedTaskListener) {
        this.mFinishedTaskListener = mFinishedTaskListener;
    }

    @Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(context instanceof Activity)
			lockScreenOrientation();
		if (this.waitMessage != null) {
			openProgress();
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			return transaction.execute();
		} catch(Exception e) { // Caso haja uma exceção não tratada pela implementação de Transaction.
			Log.e("transactionError", e.getMessage(), e);
			return false;
		} finally {
			publishProgress(100);
		}
	}

	@Override
	protected void onPostExecute(Boolean ok) {
		super.onPostExecute(ok);
        if(mFinishedTaskListener != null) {
            mFinishedTaskListener.onFinished(this);
        }
		unlockScreenOrientation();
		transaction.updateView(ok);
		if(updateListener != null) updateListener.onUpdateView(transaction, ok);

		closeProgress();
	}

    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer value = values[0];
        if(value != null && progress != null)
            progress.setProgress(values[0]);
    }

    private void openProgress() {
		progress = AndroidDialogs.showProgressDialog(context, hasProgressBar, "", waitMessage);
		progress.setCancelable(true);
		//progress.setCanceledOnTouchOutside(true);
		progress.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
                AlertDialog.Builder builder = AndroidDialogs.createAlertDialog(context,
                        context.getString(R.string.cancel), context.getString(R.string.sure_cancel));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (TransactionTask.this.getStatus().equals(Status.RUNNING)) {
                            TransactionTask.this.cancel(true);
                        }
                        transaction.onCancel();
                        if(mFinishedTaskListener != null) {
                            mFinishedTaskListener.onFinished(TransactionTask.this);
                        }
                    }
                });
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
						if(progress != null)
                        	progress.show();
                    }
                });
                builder.show();
			}
		});
	}

	/**
	 * Dismiss progress if exist
	 */
	void closeProgress() {
		if (progress != null) {
			try {
				progress.dismiss();
			} catch (Exception e){
				Log.e("transactionError", e.getMessage(), e);
			}

			progress = null;
		}
	}

	/**
	 * lock the screen orientation
	 */
	private void lockScreenOrientation() {
		try {
			currentOrientation = context.getResources().getConfiguration().orientation;
			if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
				((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} catch (ClassCastException e){
			//Log.d(TAG, e.getMessage(), e);
		}
	}

	/**
	 * unlock the screen orientation
	 */
	private void unlockScreenOrientation() {
		try {
			if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
				((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		} catch (ClassCastException e){
			//Log.d(TAG, e.getMessage(), e);
		}
	}
}