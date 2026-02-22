package com.wsclient.api.business.response;

import java.util.List;

public record BusinessProfileResponse(
        String messagingProduct,
        String address,
        String description,
        String vertical,
        String about,
        String email,
        List<String> websites,
        String profilePictureUrl) {
}