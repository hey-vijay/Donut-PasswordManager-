package vijay.bhadolia.key.ui.dialogs;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import vijay.bhadolia.key.Interfaces.DeleteConfirmationInterface;
import vijay.bhadolia.key.R;
import vijay.bhadolia.key.model.Password;

public class DeleteConfirmationDialog {

    public static AlertDialog getDialog(FragmentActivity activity, Password password, DeleteConfirmationInterface listener) {
                // set message, title, and icon
        String msz = "Delete " + password.getTitle() + "?";
        AlertDialog alertDialog =  new AlertDialog.Builder(activity)
                .setTitle(R.string.delete)
                .setMessage(msz)
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton(R.string.delete, (dialog, whichButton) -> {
                    listener.onPositiveButtonClicked();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();
        return alertDialog;
    }
}
