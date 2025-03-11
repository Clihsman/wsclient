package com.wsclient.cloud.api.messages.request;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;

/**
 * The Components Object contains the following fields
 */
@Builder
public record Component(
        /**
         * <strong>
         * Required.
         * </strong>
         * <p>
         * Describes the component type.
         * </p>
         * </p>
         * <strong>Values</strong>: <code>header</code>, <code>body</code>,
         * <code>button</code>
         * For text-based templates, only <code>body</code> is supported.
         * </p>
         */
        ComponentType type,
        /**
         * <strong>Required when type is</strong> <code>button</code>.
         * <p>
         * The namespace of the template.
         * </p>
         */
        Parameter parameters) {
    public enum ComponentType {
        HEADER("header"),
        BODY("body"),
        BUTTON("button");

        private final String value;

        ComponentType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
