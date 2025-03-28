package com.wsclient.api.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wsclient.api.validators.ConfigValidator;

public class WhatsAppMediaServiceImpl implements WhatsAppMediaService {

    private String whatsappApiUrl;
    private String phoneNumberId;
    private String token;

    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token) {
        this.whatsappApiUrl = whatsappApiUrl;
        this.phoneNumberId = phoneNumberId;
        this.token = token;

        ConfigValidator.validateConfig(whatsappApiUrl, phoneNumberId, token);
    }

    @Override
    public CompletableFuture<String> uploadMedia(InputStream media, String fileName, String type) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpPost httpPost = createHttpPost();

                final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addPart("file", new InputStreamBody(media, ContentType.create(type), fileName));
                builder.addTextBody("type", type);
                final HttpEntity entity = builder.build();
                httpPost.setEntity(entity);

                sendRequest(httpPost);
                return "";
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<String> uploadMedia(String filePath, String fileName, String type) throws IOException {

        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpPost httpPost = createHttpPost();

                final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                final File file = new File(filePath);
                builder.addPart("file", new FileBody(file, ContentType.create(type), fileName));
                builder.addTextBody("type", type);
                final HttpEntity entity = builder.build();
                httpPost.setEntity(entity);

                sendRequest(httpPost);
                return "";
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private HttpPost createHttpPost() throws JsonProcessingException {
        final HttpPost httppost = new HttpPost(String.format("%s/%s/media", whatsappApiUrl, phoneNumberId));
        httppost.addHeader("Authorization", String.format("Bearer %s", token));
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("Content-Type", "multipart/form-data");
        return httppost;
    }

    private void sendRequest(HttpPost httpPost) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .build()) {

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                EntityUtils.consume(responseEntity);
                response.getStatusLine().getStatusCode();
            }
        }
    }
}
