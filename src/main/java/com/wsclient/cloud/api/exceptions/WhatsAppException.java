package com.wsclient.cloud.api.exceptions;

/**
 * WhatsAppException
 */
public class WhatsAppException extends Exception {
    /**
     * WhatsAppException
     * 
     * @param message message
     */
    public WhatsAppException(String message) {
        super(message);
    }

    /**
     * WhatsAppException
     * 
     * @param message message
     * @param cause   cause
     */
    public WhatsAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
