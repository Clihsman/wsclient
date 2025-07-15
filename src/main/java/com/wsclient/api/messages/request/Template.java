package com.wsclient.api.messages.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Template Object contains the following fields
 * 
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {
        /**
         * <strong>
         * Required.
         * </strong>
         * <p>
         * The name of the template.
         * </p>
         */
        private String name;
        /**
         * <strong>
         * Required.
         * </strong>
         * <p>
         * Specifies a language object. Specifies the language the template may be
         * rendered in.
         * </p>
         * 
         * <p>
         * Only the <code>deterministic</code> language policy works with media template
         * messages.
         * </p>
         */
        private Language language;
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * An array of components objects that contain the parameters of the message.
         * </p>
         */
        private List<Component> components;
}
