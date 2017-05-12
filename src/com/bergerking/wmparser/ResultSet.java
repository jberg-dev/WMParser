package com.bergerking.wmparser;

/**
 * Created by Bergerking on 2017-04-27.
 */
public enum ResultSet {
    OK  (0, "Ok"),
    ERROR (1, "ERROR"),
    COMPLAINT (2, "The data set has yielded a complaint, please review the message");

    private final int code;
    private final String message;

    ResultSet(int code, String message){
        this.code = code;
        this.message = message;
    }
}
