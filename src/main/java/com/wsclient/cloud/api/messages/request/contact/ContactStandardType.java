package com.wsclient.cloud.api.messages.request.contact;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ContactStandardType
 */
public enum ContactStandardType {
    /**
     * HOMEF
     */
    HOME("HOME"),
    /**
     * WORKF
     */
    WORK("WORK");

    private final String value;

    ContactStandardType(String value) {
        this.value = value;
    }

    /**
     * getValue
     * 
     *  @return value
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}
