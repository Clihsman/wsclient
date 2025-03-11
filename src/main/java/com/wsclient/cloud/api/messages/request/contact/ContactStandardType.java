package com.wsclient.cloud.api.messages.request.contact;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContactStandardType {
    HOME("HOME"),
    WORK("WORK");

    private final String value;

    ContactStandardType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
