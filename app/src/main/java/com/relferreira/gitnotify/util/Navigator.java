package com.relferreira.gitnotify.util;

import android.content.Context;
import android.content.Intent;

import com.relferreira.gitnotify.ui.login.LoginActivity;

/**
 * Created by relferreira on 1/28/17.
 */
public class Navigator {

    public void goToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
