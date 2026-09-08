package com.prova.hackaton_parte_1.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

final class CloudinaryMultipartBody {
    private CloudinaryMultipartBody() { }

    static void writeTextField(OutputStream output, String boundary, String name, String value)
            throws IOException {
        write(output, "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n");
    }

    static void writeFileHeader(OutputStream output, String boundary, String filename, String mimeType)
            throws IOException {
        write(output, "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
                + "Content-Type: " + mimeType + "\r\n\r\n");
    }

    static void finish(OutputStream output, String boundary) throws IOException {
        write(output, "\r\n--" + boundary + "--\r\n");
    }

    private static void write(OutputStream output, String value) throws IOException {
        output.write(value.getBytes(StandardCharsets.UTF_8));
    }
}
