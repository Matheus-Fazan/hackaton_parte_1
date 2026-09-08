package com.prova.hackaton_parte_1.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class CloudinaryUploadConfigTest {
    @Test public void buildsSecureImageUploadEndpoint() {
        assertEquals(
                "https://api.cloudinary.com/v1_1/dikitk54o/image/upload",
                CloudinaryUploadConfig.endpoint("dikitk54o")
        );
    }

    @Test public void rejectsEmptyCloudName() {
        assertThrows(IllegalArgumentException.class, () -> CloudinaryUploadConfig.endpoint("  "));
    }
}
