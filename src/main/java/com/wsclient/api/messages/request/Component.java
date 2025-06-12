package com.wsclient.api.messages.request;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Data;

/**
 * The Components Object contains the following fields
 * 
 * @param type       type
 * @param parameters parameters
 */
@Builder
@Data
public class Component {

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
    private ComponentType type;
    /**
     * <strong>Required when type is</strong> <code>button</code>.
     * <p>
     * The namespace of the template.
     * </p>
     */
    private Parameter parameters;

    /**
     * ComponentType
     */
    public enum ComponentType {
        /**
         * HEADER
         */
        HEADER("header"),
        /**
         * BODY
         */
        BODY("body"),
        /**
         * BUTTON
         */
        BUTTON("button");

        private final String value;

        ComponentType(String value) {
            this.value = value;
        }

        /**
         * getValue
         * 
         * @return value
         */
        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
