package com.wsclient.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface WhatsAppMediaService {
    CompletableFuture<String> uploadMedia(InputStream media, String fileName, String type) throws IOException;

    CompletableFuture<String> uploadMedia(String filePath, String fileName, String type) throws IOException;
}
