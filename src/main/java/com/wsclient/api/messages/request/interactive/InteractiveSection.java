package com.wsclient.api.messages.request.interactive;

import java.util.List;

import lombok.Builder;

/**
 * InteractiveSection
 * 
 * @param title title
 * @param rows  rows
 */
@Builder
public record InteractiveSection(
                /**
                 * <strong>
                 * Required if the message has more than one section.
                 * </strong>
                 * 
                 * <p>
                 * The title of the <code>section</code>.
                 * </p>
                 * 
                 * Maximum length: 24 characters.
                 */
                String title,
                /**
                 * <strong>
                 * Required for List Messages.
                 * </strong>
                 * 
                 * <p>
                 * Contains a list of rows. You can have a
                 * maximum of 10 rows across your sections.
                 * </p>
                 * 
                 * Each row must have a <code>title</code> (Maximum length: 24 characters) and
                 * an <code>id</code> (Maximum
                 * length: 200 characters). You can add a <code>description</code> (Maximum
                 * length: 72
                 * characters), but it is optional.
                 */
                List<InteractiveSectionRow> rows

) {
}