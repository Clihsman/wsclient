package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.messages.response.WhatsAppResponse;

// Regression coverage for the response models WhatsAppServiceImpl deserializes
// with its internal ObjectMapper — that mapper has no snake_case naming
// strategy configured, so every field mapping to a snake_case JSON key
// (e.g. Meta's "wa_id") relies entirely on an explicit @JsonProperty. A
// missing annotation here fails silently at compile time and only breaks at
// runtime, on the very first real API response — see ContactData#waId.
public class WhatsAppResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializesContactsWaIdFromSnakeCaseJson() throws Exception {
        String body = """
                {
                  "messaging_product": "whatsapp",
                  "contacts": [
                    {"input": "573000000000", "wa_id": "573000000000"}
                  ],
                  "messages": [
                    {"id": "wamid.HBg=", "message_status": "accepted"}
                  ]
                }
                """;

        WhatsAppResponse response = mapper.readValue(body, WhatsAppResponse.class);

        assertEquals("whatsapp", response.messagingProduct());
        assertEquals("573000000000", response.contacts().get(0).waId());
        assertEquals("wamid.HBg=", response.messages().get(0).id());
        assertEquals("accepted", response.messages().get(0).messageStatus());
    }
}
