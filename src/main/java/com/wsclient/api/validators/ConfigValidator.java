package com.wsclient.api.validators;

/**
 * ConfigValidator
 */
public final class ConfigValidator {

    private static final String ERROR_API_URL_NULL = "WhatsApp API URL cannot be null or empty.";
    private static final String ERROR_API_URL_INVALID = "Invalid API URL. It must start with 'http' or 'https'.";
    private static final String ERROR_PHONE_ID_NULL = "Phone number ID cannot be null or empty.";
    private static final String ERROR_PHONE_ID_INVALID = "Phone number ID must contain only digits.";
    private static final String ERROR_TOKEN_NULL = "Token cannot be null or empty.";

    /**
     * ConfigValidator
     */
    private ConfigValidator() {
    }

    /**
     * Validates the configuration parameters required for the WhatsApp API
     * integration.
     * 
     * @param whatsappApiUrl whatsappApiUrl
     * @param phoneNumberId  phoneNumberId
     * @param token          token
     * 
     *                       <p>
     *                       This method ensures that the following conditions are
     *                       met:
     *                       <ul>
     *                       <li>The WhatsApp API URL is not null or empty.</li>
     *                       <li>The WhatsApp API URL starts with "http" or
     *                       "https".</li>
     *                       <li>The phone number ID is not null or empty.</li>
     *                       <li>The phone number ID contains only digits.</li>
     *                       <li>The authentication token is not null or empty.</li>
     *                       </ul>
     *                       If any of these conditions are not met, an
     *                       {@link IllegalArgumentException}
     *                       is thrown.
     * 
     * @throws IllegalArgumentException if any of the configuration parameters are
     *                                  invalid.
     */
    public static void validateConfig(String whatsappApiUrl, String phoneNumberId, String token)
            throws IllegalArgumentException {
        validateApiUrl(whatsappApiUrl);
        validatePhoneNumberId(phoneNumberId);
        validateToken(token);
    }

    private static void validateApiUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_API_URL_NULL);
        }
        if (!url.startsWith("http")) {
            throw new IllegalArgumentException(ERROR_API_URL_INVALID);
        }
    }

    private static void validatePhoneNumberId(String phoneId) {
        if (phoneId == null || phoneId.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PHONE_ID_NULL);
        }
        if (!phoneId.matches("\\d+")) {
            throw new IllegalArgumentException(ERROR_PHONE_ID_INVALID);
        }
    }

    private static void validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_TOKEN_NULL);
        }
    }
}