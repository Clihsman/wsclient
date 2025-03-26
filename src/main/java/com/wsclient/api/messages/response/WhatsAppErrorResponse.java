package com.wsclient.api.messages.response;

import com.wsclient.api.messages.request.Error;

/**
 * WhatsAppErrorResponse
 * 
 * @param error error
 */
public record WhatsAppErrorResponse(Error error) {

}