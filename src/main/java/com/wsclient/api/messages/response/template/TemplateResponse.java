package com.wsclient.api.messages.response.template;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TemplateResponse {
    private String id;
    private String name;
    private String language;
    private String status;
    private String category;
    @JsonProperty("parameter_format")
    private String parameterFormat;
    private List<Component> components;
}
