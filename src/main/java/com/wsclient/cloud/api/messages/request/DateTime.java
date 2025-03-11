package com.wsclient.cloud.api.messages.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;

/**
 * The DateTime Object contains the following fields
 */
@Builder
public record DateTime(
        /**
         * <strong>
         * Required.
         * </strong>
         * <p>
         * The default text if localization fails.
         * </p>
         */
        @JsonProperty("fallback_value") String fallbackValue,
        /**
         * <strong>Optional.</strong>
         * <p>
         * If it is different from the value derived from the date (if specified), use
         * the derived value. Both strings and numbers are accepted.
         * </p>
         */
        @JsonProperty("day_of_week") DateTimeDayOfWeek dayOfWeek,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Specifies the year.
         * </p>
         */
        Integer yaer,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Specifies the month.
         * </p>
         */
        Integer month,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Specifies the day of the month.
         * </p>
         */
        Integer dayOfMonth,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Specifies the hour.
         * </p>
         */
        Integer hour,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Specifies the minute.
         * </p>
         */
        Integer minute,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * The type of calendar.
         * </p>
         * <p>
         * <strong>Values</strong>: <code>"GREGORIAN"</code> or <code>"SOLAR_HIJRI"</code>.
         * </p>
         */
        DateTimeCalendar calendar) {
    public enum DateTimeDayOfWeek {
        MONDAY("MONDAY"),
        TUESDAY("TUESDAY"),
        WEDNESDAY("WEDNESDAY"),
        THURSDAY("THURSDAY"),
        FRIDAY("FRIDAY"),
        SATURDAY("SATURDAY"),
        SUNDAY("SUNDAY");

        private final String value;

        DateTimeDayOfWeek(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public enum DateTimeCalendar {
        GREGORIAN("GREGORIAN"),
        SOLAR_HIJRI("SOLAR_HIJRI");

        private final String value;

        DateTimeCalendar(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
