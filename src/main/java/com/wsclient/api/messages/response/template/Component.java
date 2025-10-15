package com.wsclient.api.messages.response.template;

import java.util.List;

import lombok.Getter;

@Getter
public class Component {
    private String type;
    private String format;
    private String text;
    private List<Parameter> parameters;
    private List<Button> buttons;
    private BodyTextNamedParams example;
}
