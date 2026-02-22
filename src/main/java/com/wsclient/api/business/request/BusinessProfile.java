package com.wsclient.api.business.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessProfile {
    @Builder.Default
    private String messagingProduct = "whatsapp";
    private String address;
    private String description;
    private String vertical;
    private String about;
    private String email;
    private List<String> websites;
    private String profilePictureUrl;
}