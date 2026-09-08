package com.prova.hackaton_parte_1.ui.common;

import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.InputStream;
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
            try (InputStream stream = source.startsWith("content://")
                    ? view.getContext().getContentResolver().openInputStream(android.net.Uri.parse(source))
                    : new URL(source).openStream()) {
                if (stream == null) return;
                android.graphics.Bitmap bitmap = BitmapFactory.decodeStream(stream);
                view.post(() -> { if (source.equals(view.getTag())) view.setImageBitmap(bitmap); });
            } catch (Exception ignored) { }
        });
    }
}
