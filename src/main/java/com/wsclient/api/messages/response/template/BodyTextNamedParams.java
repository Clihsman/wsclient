package com.wsclient.api.messages.response.template;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class BodyTextNamedParams {
    @JsonProperty("body_text_named_params")
    private List<BodyTextNamedParam> bodyTextNamedParams;
}
