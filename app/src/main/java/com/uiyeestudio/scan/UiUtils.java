package com.uiyeestudio.scan;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Michael on 2017/8/29.
 */

public class UiUtils {

    public static void showDialog(Context context,
                            String title,
                            String msg,
                            String posText,
                            String negText,
                            DialogInterface.OnClickListener posClick,
                            DialogInterface.OnClickListener nevClick,
                            boolean cancelable) {

        new AlertDialog.Builder(context).
                setTitle(title).
                setMessage(msg).
                setPositiveButton(posText, posClick).
                setNegativeButton(negText, nevClick).
                setCancelable(cancelable).
                show();

    }
}
