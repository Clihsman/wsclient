package com.wsclient.api.constants;

/**
 * Defines a set of constants related to WhatsApp messaging limits,
 * particularly for text messages and interactive message components like
 * buttons and list rows.
 * <p>
 * This class is not meant to be instantiated.
 */
public final class WhatsAppConstants {

    /**
     * The maximum number of characters allowed in a text message.
     */
    public static final int MESSAGE_MAX_TEXT = 4096;

    /**
     * The minimum number of characters required in a text message.
     */
    public static final int MESSAGE_MIN_TEXT = 1;

    /**
     * The maximum number of buttons allowed in an interactive message.
     */
    public static final int INTERACTIVE_MAX_BUTTONS = 3;

    /**
     * The minimum number of buttons required in an interactive message.
     */
    public static final int INTERACTIVE_MIN_BUTTONS = 1;

    /**
     * The maximum number of rows allowed in an interactive list message.
     */
    public static final int INTERACTIVE_MAX_LIST_ROWS = 10;

    /**
     * The minimum number of rows required in an interactive list message.
     */
    public static final int INTERACTIVE_MIN_LIST_ROWS = 1;

    /**
     * The maximum length (in characters) for the title of a row in a list message.
     */
    public static final int INTERACTIVE_MAX_ROW_TITLE_LENGTH = 24;

    /**
     * The maximum length (in characters) for the description of a row in a list
     * message.
     */
    public static final int INTERACTIVE_MAX_ROW_DESCRIPTION_LENGTH = 72;

    /**
     * The minimum valid value for a location's latitude.
     */
    public static final double LOCATION_MIN_LATITUDE = -90;

    /**
     * The maximum valid value for a location's latitude.
     */
    public static final double LOCATION_MAX_LATITUDE = 90;

    /**
     * The minimum valid value for a location's longitude.
     */
    public static final double LOCATION_MIN_LONGITUDE = -180;

    /**
     * The maximum valid value for a location's longitude.
     */
    public static final double LOCATION_MAX_LONGITUDE = 180;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private WhatsAppConstants() {
        // Prevent instantiation
    }
}
