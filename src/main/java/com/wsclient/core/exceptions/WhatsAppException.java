package com.wsclient.core.exceptions;

/**
 * Custom exception for handling WhatsApp API errors.
 * <p>
 * This exception is thrown when the WhatsApp API returns an error response.
 * It includes details such as the error type, code, subcode, and a Facebook
 * trace ID
 * for debugging.
 * </p>
 *
 * <h3>Example JSON Error Response:</h3>
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
public class WhatsAppException extends Exception {

    private String type;
    private Integer code;
    private Integer errorSubcode;
    private String fbtraceId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getErrorSubcode() {
        return errorSubcode;
    }

    public void setErrorSubcode(Integer errorSubcode) {
        this.errorSubcode = errorSubcode;
    }

    public String getFbtraceId() {
        return fbtraceId;
    }

    public void setFbtraceId(String fbtraceId) {
        this.fbtraceId = fbtraceId;
    }
}