package com.wsclient.api.messages.response.template;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class BodyTextNamedParam {
    @JsonProperty("param_name")
    private String paramName;

    private String example;
}
