package com.prova.hackaton_parte_1.data;

final class CloudinaryUploadConfig {
    private static final String BASE_URL = "https://api.cloudinary.com/v1_1/";

    private CloudinaryUploadConfig() { }

    static String endpoint(String cloudName) {
        if (cloudName == null || !cloudName.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Cloud name inválido.");
        }
        return BASE_URL + cloudName + "/image/upload";
    }
}
