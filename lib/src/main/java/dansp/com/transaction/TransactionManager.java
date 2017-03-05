package dansp.com.transaction;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class TransactionManager implements TransactionTask.FinishedTaskListener {
	private Context context;
	private TransactionTask task;
	private static int NUM_TASK = 10; //Default Value
	private OnTransactionCancelListener defaultCancelListener = new OnTransactionCancelListener() {
		@Override
		public void onCancel(boolean cancel) { 
			Log.i("cancel", "Trasacao Cancelada!");
		}
	};
	private Map<TransactionTask, OnTransactionCancelListener> taskPool;

	interface OnTransactionCancelListener {
		void onCancel(boolean cancel);
	}


	public TransactionManager(Context context) {
		this.context = context;
		taskPool = new HashMap<>(NUM_TASK);
	}

	public TransactionManager(Context context, int numTask) {
		this.context = context;
		NUM_TASK = numTask;
		taskPool = new HashMap<>(NUM_TASK);
	}
	/**
	 * Inicia uma transação interna no aparelho numa thread separada.
	 * @param transaction implementação de Transaction que vai executar a tarefa.
	 * @param waitMessage message de espera ao usuário.
	 */
	public void startTransaction(Transaction transaction, String waitMessage, boolean hasProgressBarUpdate) throws IllegalThreadStateException {
		task = new TransactionTask(context, transaction, waitMessage, hasProgressBarUpdate);
		init();

		if(hasProgressBarUpdate) {
			Dispatcher.getInstance().setProgressBar(new ProgressUpdate() {
				@Override
				public void onUpdateProgressBar(int actualSize) {
					Log.d("progress", actualSize + "");
					task.onProgressUpdate(actualSize);
				}
			});
		}
	}

	public void startTransaction(Transaction transaction, String waitMessage) throws IllegalThreadStateException {
		task = new TransactionTask(context, transaction, waitMessage, false);
		init();
	}

	public void startTransaction(Transaction transaction, int StringId, boolean hasProgressBarUpdate) throws IllegalThreadStateException {
		startTransaction(transaction, this.context.getString(StringId), hasProgressBarUpdate);
	}

	public void startTransaction(Transaction transaction, int StringId) throws IllegalThreadStateException {
		startTransaction(transaction, this.context.getString(StringId), false);
	}

	public void startTransaction(Transaction transaction) throws IllegalThreadStateException {
		task = new TransactionTask(this.context, transaction);
		init();
	}

	public boolean lock(){
		try {
			return task.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean lock(long timeOutInSec){
		try {
			return task.get(timeOutInSec, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void init(){
		task.setFinishedTaskListener(this);
		taskPool.put(task, defaultCancelListener);
		task.execute();

		if(taskPool.size() > NUM_TASK){
			throw new IndexOutOfBoundsException("Cannot be more than " + NUM_TASK + " thread by intance");
		}
	}

	@Override
	public void onFinished(TransactionTask task) {
		if(taskPool != null && task != null && taskPool.containsKey(task)) {
			taskPool.remove(task);
		}
	}

	/**
	 * Cancela todas as Transacoes gerenciadas por este TransactionManager.
	 */
	public void cancelTransaction() {
		for (TransactionTask task : taskPool.keySet()) {
			if (task != null) {
				if (task.getStatus().equals(AsyncTask.Status.RUNNING)) {
					boolean cancel = task.cancel(true);
					taskPool.get(task).onCancel(cancel);
				}
				task.closeProgress();
			}
		}
	}

	public void setUpdateListener(TransactionTask.OnUpdateView updateListener) {
		if(task != null) task.updateListener = updateListener;
	}
}