package com.wsclient.api.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WhatsAppMediaServiceImpl implements WhatsAppMediaService {

    @Override
    public String uploadMedia(InputStream media, String fileName, String type) throws IOException {

        final HttpPost httppost = new HttpPost("https://host/upload");
        httppost.addHeader("Authorization", "Bearer xxx");
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("Content-Type", "multipart/form-data");

        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        final File file = new File("c:\\tmp\\myfile.pdf");
        builder.addPart("file", new FileBody(file));
        builder.addTextBody("type", "file");
        final HttpEntity entity = builder.build();
        httppost.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .build()) {

            try (CloseableHttpResponse response = httpClient.execute(httppost)) {
                HttpEntity responseEntity = response.getEntity();
                EntityUtils.consume(responseEntity);
                response.getStatusLine().getStatusCode();
            }
        }

        throw new UnsupportedOperationException("Unimplemented method 'uploadMedia'");
    }

}
