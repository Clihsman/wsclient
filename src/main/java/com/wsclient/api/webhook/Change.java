package com.wsclient.api.webhook;

/**
 * Change
 * 
 * @param value value
 * @param field field
 */
public record Change(
                /**
                 * A value object. Contains details of the changes related to the specified
                 * field.
                 */
                Value value,
                /**
                 * Contains the type of notification you are getting on that Webhook. Currently,
                 * the only option for this API is <code>“messages”</code>.
                 */
                String field) {
}
