package com.aabello.iwalk.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.aabello.iwalk.R;

public class DisclaimerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder disclaimerDialog = new AlertDialog.Builder(context);

        disclaimerDialog.setTitle(R.string.disclaimer_dialog_title)
                .setMessage(R.string.disclaimer_dialog_message)
                .setNegativeButton(R.string.ok_button_label, null);

        AlertDialog alertDialog = disclaimerDialog.create();
        return alertDialog;
    }
}
