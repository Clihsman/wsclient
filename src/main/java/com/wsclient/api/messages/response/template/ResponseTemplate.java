package com.wsclient.api.messages.response.template;

import java.util.List;

import lombok.Getter;

@Getter
public class ResponseTemplate {
    private String name;
    private Language language;
    private List<Component> components;
}
