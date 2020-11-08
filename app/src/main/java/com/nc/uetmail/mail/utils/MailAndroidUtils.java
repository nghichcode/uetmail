package com.nc.uetmail.mail.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nc.uetmail.R;

import java.io.File;

import androidx.annotation.NonNull;

public class MailAndroidUtils {
    public static final String ROOT_URI = "file://";
    public static final String ROOT_FOLDER = Environment.getExternalStorageDirectory()
        + File.separator + "uetmail_data";

    public static void hideKeyborad(@NonNull Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showCtxToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void showCtxToast(Context context, int resid) {
        Toast.makeText(context, context.getResources().getString(resid), Toast.LENGTH_SHORT).show();
    }
}
