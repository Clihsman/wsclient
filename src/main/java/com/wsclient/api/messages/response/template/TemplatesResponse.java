package com.wsclient.api.messages.response.template;

import java.util.List;

import lombok.Getter;

@Getter
public class TemplatesResponse {
    private List<TemplateResponse> data;
    private Paging paging;
}