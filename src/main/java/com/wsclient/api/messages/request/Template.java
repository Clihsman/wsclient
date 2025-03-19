package com.wsclient.cloud.api.messages.request;

import java.util.List;

import lombok.Builder;

/**
 * The Template Object contains the following fields
 * 
 * @param name       name
 * @param language   language
 * @param components components
 */
@Builder
public record Template(
                /**
                 * <strong>
                 * Required.
                 * </strong>
                 * <p>
                 * The name of the template.
                 * </p>
                 */
                String name,
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
                Language language,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * <p>
                 * An array of components objects that contain the parameters of the message.
                 * </p>
                 */
                List<Component> components

) {
}
