package com.wsclient.core.exceptions;

import lombok.Getter;

/**
 * Custom exception for handling WhatsApp API errors.
 * <p>
 * This exception is thrown when the WhatsApp API returns an error response.
 * It includes details such as the error type, code, subcode, and a Facebook
 * trace ID
 * for debugging.
 * </p>
 *
 * <b>Example JSON Error Response:</b>
 * 
 * <pre>
 * {
 *   "error": {
 *     "message": "(#131006) Resource not found",
 *     "type": "OAuthException",
 *     "code": 131006,
 *     "error_data": {
 *       "messaging_product": "whatsapp",
 *       "details": "unknown contact"
 *     },
 *     "error_subcode": 2494007,
 *     "fbtrace_id": "Az8or2yhqkZfEZ-_4Qn_Bam"
 *   }
 * }
 * </pre>
 */
@Getter
public class WhatsAppException extends Exception {

    /**
     * The type of error.
     */
    private final String type;

    /**
     * The error code.
     */
    private final Integer code;

    /**
     * The specific subcode for the error.
     */
    private final Integer errorSubcode;

    /**
     * The Facebook trace ID for debugging.
     */
    private final String fbtraceId;

    /**
     * Constructs a new WhatsAppException with the specified error details.
     *
     * @param message      A description of the error.
     * @param type         The type of error returned by the WhatsApp API.
     * @param code         The specific error code.
     * @param errorSubcode Additional subcode providing more context.
     * @param fbtraceId    A trace ID for tracking the error in Facebook's system.
     */
    public WhatsAppException(String message, String type, Integer code, Integer errorSubcode, String fbtraceId) {
        super(message);
        this.type = type;
        this.code = code;
        this.errorSubcode = errorSubcode;
        this.fbtraceId = fbtraceId;
    }

    /**
     * Constructs a new WhatsAppException with the specified error details and
     * cause.
     *
     * @param message      A description of the error.
     * @param type         The type of error returned by the WhatsApp API.
     * @param code         The specific error code.
     * @param errorSubcode Additional subcode providing more context.
     * @param fbtraceId    A trace ID for tracking the error in Facebook's system.
     * @param cause        The underlying cause of the exception.
     */
    public WhatsAppException(String message, String type, Integer code, Integer errorSubcode, String fbtraceId,
            Throwable cause) {
        super(message, cause);
        this.type = type;
        this.code = code;
        this.errorSubcode = errorSubcode;
        this.fbtraceId = fbtraceId;
    }
}