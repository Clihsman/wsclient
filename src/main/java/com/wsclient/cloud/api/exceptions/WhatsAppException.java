package com.wsclient.cloud.api.exceptions;

public class WhatsAppException extends Exception {
    public WhatsAppException(String message) {
        super(message);
    }

    public WhatsAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
