package com.wsclient.api.messages.request.contact;

/**
 * ContactUrl
 * 
 * @param url  url
 * @param type type
 */
public record ContactUrl(
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * The URL.
                 * </p>
                 */
                String url,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * Standard Values: <code>HOME</code>, <code>WORK</code>
                 * </p>
                 */
                ContactStandardType type) {
}
