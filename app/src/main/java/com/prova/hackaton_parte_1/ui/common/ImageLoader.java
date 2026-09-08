package com.prova.hackaton_parte_1.ui.common;

import android.graphics.ImageDecoder;
import android.net.Uri;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageLoader {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private ImageLoader() { }

    public static void load(ImageView view, String source) {
        view.setImageDrawable(null);
        view.setTag(source);
        if (source == null || source.isEmpty()) return;
        EXECUTOR.execute(() -> {
            try {
                ImageDecoder.Source input;
                if (source.startsWith("content://")) {
                    input = ImageDecoder.createSource(view.getContext().getContentResolver(), Uri.parse(source));
                } else {
                    java.net.URLConnection connection = new URL(source).openConnection();
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(30000);
                    try (InputStream stream = connection.getInputStream(); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[8192];
                        for (int size; (size = stream.read(buffer)) != -1;) {
                            if (bytes.size() + size > 32 * 1024 * 1024) throw new java.io.IOException("Imagem muito grande");
                            bytes.write(buffer, 0, size);
                        }
                        input = ImageDecoder.createSource(ByteBuffer.wrap(bytes.toByteArray()));
                    }
                }
                android.graphics.Bitmap bitmap = ImageDecoder.decodeBitmap(input, (decoder, info, src) -> {
                    int width = info.getSize().getWidth();
                    int height = info.getSize().getHeight();
                    double scale = Math.min(1.0, 1600.0 / Math.max(width, height));
                    decoder.setTargetSize(Math.max(1, (int) (width * scale)), Math.max(1, (int) (height * scale)));
                });
                view.post(() -> { if (source.equals(view.getTag())) view.setImageBitmap(bitmap); });
            } catch (Exception ignored) { }
        });
    }
}
