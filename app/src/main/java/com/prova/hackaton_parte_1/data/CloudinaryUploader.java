package com.prova.hackaton_parte_1.data;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.prova.hackaton_parte_1.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CloudinaryUploader {
    public interface Callback { void complete(String url, Exception error); }
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private CloudinaryUploader() { }

    public static boolean isConfigured(Context context) {
        return !context.getString(R.string.cloudinary_cloud_name).startsWith("SEU_")
                && !context.getString(R.string.cloudinary_upload_preset).startsWith("SEU_");
    }

    public static void upload(Context context, Uri photo, Callback callback) {
        EXECUTOR.execute(() -> {
            String result = null;
            Exception failure = null;
            try {
                String cloud = context.getString(R.string.cloudinary_cloud_name);
                String preset = context.getString(R.string.cloudinary_upload_preset);
                String boundary = "----TikTokTech" + UUID.randomUUID();
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.cloudinary.com/v1_1/" + cloud + "/image/upload").openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                try (OutputStream output = connection.getOutputStream(); InputStream input = context.getContentResolver().openInputStream(photo)) {
                    if (input == null) throw new IllegalStateException("Não foi possível ler a foto.");
                    writeField(output, boundary, "upload_preset", preset);
                    output.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"photo.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n").getBytes());
                    byte[] buffer = new byte[8192];
                    for (int size; (size = input.read(buffer)) != -1;) output.write(buffer, 0, size);
                    output.write(("\r\n--" + boundary + "--\r\n").getBytes());
                }
                InputStream response = connection.getResponseCode() / 100 == 2 ? connection.getInputStream() : connection.getErrorStream();
                String body = readBody(response);
                if (connection.getResponseCode() / 100 != 2) throw new IllegalStateException(body);
                result = new JSONObject(body).getString("secure_url");
            } catch (Exception error) {
                failure = error;
            }
            String url = result;
            Exception error = failure;
            new Handler(Looper.getMainLooper()).post(() -> callback.complete(url, error));
        });
    }

    private static void writeField(OutputStream output, String boundary, String name, String value) throws Exception {
        output.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + value + "\r\n").getBytes());
    }

    private static String readBody(InputStream input) throws Exception {
        if (input == null) return "Resposta vazia.";
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            for (int size; (size = stream.read(buffer)) != -1;) output.write(buffer, 0, size);
            return output.toString();
        }
    }
}
