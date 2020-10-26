package com.nc.uetmail.mail.utils.object;

public class ErrorMessage {
    public String message;
    public int code;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
