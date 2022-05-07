package com.mobileapplication.mymovie_10.helpers;

import android.app.AlertDialog;
import android.content.Context;

public class Dialog {

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Weiter", null);
        dialogBuilder.setCancelable(false);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
