package com.wsclient.cloud.api.messages.request.contact;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContactName(
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * Full name, as it normally appears
         * </p>
         */
        @JsonProperty("formatted_name") String formattedName,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * First name.
         * </p>
         */
        @JsonProperty("first_name") String firstName,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Last name.
         * </p>
         */
        @JsonProperty("last_name") String lastName,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Middle name.
         * </p>
         */
        @JsonProperty("middle_name") String middleName,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Name suffix.
         * </p>
         */
        String suffix,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Name prefix.
         * </p>
         */
        String prefix) {
}