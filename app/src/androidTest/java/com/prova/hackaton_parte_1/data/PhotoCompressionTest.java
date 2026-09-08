package com.prova.hackaton_parte_1.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import static org.junit.Assert.*;

public class PhotoCompressionTest {
    @Test public void reducesPhotoAndPreservesOriginal() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File file = File.createTempFile("compression-test", ".png", context.getCacheDir());
        try {
            Bitmap original = Bitmap.createBitmap(3200, 2400, Bitmap.Config.ARGB_8888);
            try (FileOutputStream stream = new FileOutputStream(file)) {
                assertTrue(original.compress(Bitmap.CompressFormat.PNG, 100, stream));
            } finally {
                original.recycle();
            }
            long originalSize = file.length();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            CloudinaryUploader.writePhoto(context, Uri.fromFile(file), output);
            byte[] jpeg = output.toByteArray();
            assertEquals(0xff, jpeg[0] & 0xff);
            assertEquals(0xd8, jpeg[1] & 0xff);
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, bounds);
            assertEquals(1600, bounds.outWidth);
            assertEquals(1200, bounds.outHeight);
            assertEquals(originalSize, file.length());
        } finally {
            file.delete();
        }
    }
}
