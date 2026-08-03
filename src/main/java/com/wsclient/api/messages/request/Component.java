package com.wsclient.api.messages.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Components Object contains the following fields
 * 
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
     * <strong>Required</strong> when the component carries dynamic values.
     * <p>
     * The array of parameters that fill in the placeholders of this component.
     * The WhatsApp Cloud API always expects this as a JSON array, even for a
     * single parameter.
     * </p>
     */
    private List<Parameter> parameters;

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
