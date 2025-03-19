package com.wsclient.api.webhook;

/**
 * ButtonReply
 * 
 * @param id    id
 * @param title title
 */
public record ButtonReply(
                /**
                 * The unique identifier of the button.
                 */
                String id,
                /**
                 * The title of the button.
                 */
                String title) {

}
