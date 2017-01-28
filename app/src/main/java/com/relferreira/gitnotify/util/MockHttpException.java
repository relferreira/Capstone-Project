package com.relferreira.gitnotify.util;


/**
 * Created by relferreira on 1/28/17.
 */
public class MockHttpException extends Throwable {

    private final int code;
    private final String message;

    public MockHttpException(int code, String message) {
        super("HTTP " + code + " " + message);
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() { return message; }
}
