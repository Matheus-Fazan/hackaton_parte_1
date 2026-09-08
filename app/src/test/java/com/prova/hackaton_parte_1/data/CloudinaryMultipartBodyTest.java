package com.prova.hackaton_parte_1.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class CloudinaryMultipartBodyTest {
    @Test public void writesPresetFolderAndFileAsMultipartFields() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String boundary = "test-boundary";

        CloudinaryMultipartBody.writeTextField(output, boundary, "upload_preset", "Tiktoktech");
        CloudinaryMultipartBody.writeTextField(output, boundary, "folder", "tiktoktech_salaE");
        CloudinaryMultipartBody.writeFileHeader(output, boundary, "photo.jpg", "image/jpeg");
        output.write(new byte[] {1, 2, 3});
        CloudinaryMultipartBody.finish(output, boundary);

        String body = output.toString(StandardCharsets.ISO_8859_1.name());
        assertTrue(body.contains("name=\"upload_preset\"\r\n\r\nTiktoktech"));
        assertTrue(body.contains("name=\"folder\"\r\n\r\ntiktoktech_salaE"));
        assertTrue(body.contains("filename=\"photo.jpg\"\r\nContent-Type: image/jpeg"));
        assertTrue(body.endsWith("\r\n--test-boundary--\r\n"));
    }
}
