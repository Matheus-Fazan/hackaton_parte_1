package com.prova.hackaton_parte_1.data;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.prova.hackaton_parte_1.R;
import com.prova.hackaton_parte_1.BuildConfig;

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
        return !BuildConfig.CLOUDINARY_CLOUD_NAME.isEmpty()
                && !BuildConfig.CLOUDINARY_UPLOAD_PRESET.isEmpty()
                && !BuildConfig.CLOUDINARY_FOLDER.isEmpty();
    }

    public static void upload(Context context, Uri photo, Callback callback) {
        EXECUTOR.execute(() -> {
            String result = null;
            Exception failure = null;
            try {
                String cloud = BuildConfig.CLOUDINARY_CLOUD_NAME;
                String preset = BuildConfig.CLOUDINARY_UPLOAD_PRESET;
                String folder = BuildConfig.CLOUDINARY_FOLDER;
                String boundary = "----TikTokTech" + UUID.randomUUID();
                HttpURLConnection connection = (HttpURLConnection) new URL(CloudinaryUploadConfig.endpoint(cloud)).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15_000);
                connection.setReadTimeout(30_000);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                try (OutputStream output = connection.getOutputStream(); InputStream input = context.getContentResolver().openInputStream(photo)) {
                    if (input == null) throw new IllegalStateException("Não foi possível ler a foto.");
                    CloudinaryMultipartBody.writeTextField(output, boundary, "upload_preset", preset);
                    CloudinaryMultipartBody.writeTextField(output, boundary, "folder", folder);
                    CloudinaryMultipartBody.writeFileHeader(output, boundary, "photo.jpg", "image/jpeg");
                    byte[] buffer = new byte[8192];
                    for (int size; (size = input.read(buffer)) != -1;) output.write(buffer, 0, size);
                    CloudinaryMultipartBody.finish(output, boundary);
                }
                int statusCode = connection.getResponseCode();
                InputStream response = statusCode / 100 == 2 ? connection.getInputStream() : connection.getErrorStream();
                String body = readBody(response);
                if (statusCode / 100 != 2) throw new IllegalStateException("Cloudinary respondeu HTTP " + statusCode + ": " + body);
                result = new JSONObject(body).getString("secure_url");
            } catch (Exception error) {
                failure = error;
            }
            String url = result;
            Exception error = failure;
            new Handler(Looper.getMainLooper()).post(() -> callback.complete(url, error));
        });
    }

    private static String readBody(InputStream input) throws Exception {
        if (input == null) return "Resposta vazia.";
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            for (int size; (size = stream.read(buffer)) != -1;) output.write(buffer, 0, size);
            return output.toString("UTF-8");
        }
    }
}
