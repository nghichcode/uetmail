package com.nc.uetmail.mail.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MailAndroidUtils {
    public static void hideKeyborad(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
