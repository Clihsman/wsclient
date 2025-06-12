package com.wsclient.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an error response from the WhatsApp API.
 * <p>
 * This record encapsulates detailed information about an error returned by the
 * API,
 * including a message, type, error code, and additional metadata.
 * </p>
 *
 * @param message      A description of the error.
 * @param type         The type of error encountered.
 * @param code         The HTTP status code or internal error code.
 * @param errorData    Additional error details encapsulated in an
 *                     {@link ErrorData} object.
 * @param errorSubcode A subcode providing more granular error information.
 * @param fbtraceId    A unique identifier for tracking errors in Facebook's
 *                     system.
 */
public record Error(
        String message,
        String type,
        Integer code,
        @JsonProperty("error_data") ErrorData errorData,
        @JsonProperty("error_subcode") Integer errorSubcode,
        @JsonProperty("fbtrace_id") String fbtraceId) {

}
