package com.relferreira.gitnotify.util;

import android.util.Base64;

/**
 * Created by relferreira on 1/15/17.
 */
public class CriptographyProvider {

    public String base64(String text) {
        return  Base64.encodeToString(text.getBytes(), Base64.NO_WRAP);
    }
}
