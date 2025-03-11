package com.wsclient.cloud.api.messages.request;

/**
 * The Button Parameter Object contains the following fields
 */
public record ButtonParameter(
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * Specifies the type of parameter for the button.
         * </p>
         * 
         * <p>
         * Values: <code>payload</code>,<code>text</code>
         * </p>
         */
        String type,
        /**
         * <strong>
         * Required for quick_reply buttons.
         * </strong>
         * 
         * <p>
         * Developer-defined payload that is returned when the button is clicked in
         * addition to the display text on the button.
         * </p>
         * 
         * <p>
         * For more information on usage, see Callback from a Quick Reply Button Click.
         * </p>
         */
        String payload,
        /**
         * <strong>
         * Required for url buttons.
         * </strong>
         * 
         * <p>
         * Developer-provided suffix that is appended to the predefined prefix URL in
         * the template.
         * </p>
         */
        String text) {
}
