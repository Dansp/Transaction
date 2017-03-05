package dansp.com.transaction;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AndroidDialogs {
	/**
	 * Mostra um Dialog de alerta com bot�o de confirmação
	 * @param context
	 * @param titulo
	 * @param mensagem
	 */
	public static void showAlertDialog(final Context context, final String titulo, final String mensagem) {
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle(titulo)
		.setMessage(mensagem)
		.create();
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		dialog.show();
	}

	/**
	 * Cria o cabeçalho de um dialogo pré definido.
	 * @param context
	 * @param titulo
	 * @param mensagem
	 * @return AlertDialog
	 */
	public static AlertDialog.Builder createAlertDialog(final Context context, final String titulo, final String mensagem) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(titulo)
			  .setMessage(mensagem)
			  .create();
		return dialog;
	}

	public static void showAlertDialog(final Context context,int titulo, String mensagem) {
		showAlertDialog(context, context.getString(titulo), mensagem);
	}
	public static void showAlertDialog(final Context context,int titulo, int mensagem) {
		showAlertDialog(context, context.getString(titulo), context.getString(mensagem));
	}
	public static void showAlertDialog(final Context context, int mensagem) {
		showAlertDialog(context, "", context.getString(mensagem));
	}
	public static ProgressDialog showProgressDialog(final Context context, boolean hasProgressBarUpdate, final String titulo, final String mensagem) {

        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(mensagem);

        if(hasProgressBarUpdate) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        mProgressDialog.show();

		return mProgressDialog;
	}
	public static ProgressDialog showProgressDialog(final Context context, final int mensagem) {
		return null;
	}
}
