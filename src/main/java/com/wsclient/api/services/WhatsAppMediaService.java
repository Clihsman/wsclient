package com.wsclient.api.services;

import java.io.IOException;
import java.io.InputStream;

public interface WhatsAppMediaService {
    String uploadMedia(InputStream media, String fileName, String type) throws IOException;
}
