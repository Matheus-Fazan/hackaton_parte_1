package com.prova.hackaton_parte_1.ui.post;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.prova.hackaton_parte_1.R;
import com.prova.hackaton_parte_1.data.CloudinaryUploader;
import com.prova.hackaton_parte_1.databinding.ActivityNewPostBinding;

import java.io.File;

public final class NewPostActivity extends AppCompatActivity {
    private ActivityNewPostBinding binding;
    private Uri photo;
    private Uri pendingCameraPhoto;
    private ActivityResultLauncher<Uri> camera;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(android.R.drawable.ic_media_previous);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        camera = registerForActivityResult(new ActivityResultContracts.TakePicture(), ok -> {
            if (ok && pendingCameraPhoto != null) showSelectedPhoto(pendingCameraPhoto);
            pendingCameraPhoto = null;
        });
        ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) showSelectedPhoto(uri);
        });
        binding.takePhoto.setOnClickListener(v -> {
            File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (folder == null) { Snackbar.make(binding.getRoot(), R.string.error_photo, Snackbar.LENGTH_LONG).show(); return; }
            pendingCameraPhoto = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(folder, "post_" + System.currentTimeMillis() + ".jpg"));
            camera.launch(pendingCameraPhoto);
        });
        binding.choosePhoto.setOnClickListener(v -> gallery.launch("image/*"));
        binding.publish.setOnClickListener(v -> publish());
    }

    private void publish() {
        if (photo == null) { Snackbar.make(binding.getRoot(), R.string.error_photo, Snackbar.LENGTH_LONG).show(); return; }
        binding.publish.setEnabled(false);
        if (!CloudinaryUploader.isConfigured(this)) {
            binding.publish.setEnabled(true);
            Snackbar.make(binding.getRoot(), "Configure o Cloudinary antes de publicar no feed compartilhado.", Snackbar.LENGTH_LONG).show();
            return;
        }
        showUploading(true);
        CloudinaryUploader.upload(this, photo, (url, error) -> {
            showUploading(false);
            if (error != null) {
                Snackbar.make(binding.getRoot(), R.string.upload_error, Snackbar.LENGTH_LONG).show();
                return;
            }
            binding.uploadLabel.setVisibility(View.VISIBLE);
            binding.uploadUrl.setText(url);
            binding.uploadUrl.setVisibility(View.VISIBLE);
        });
    }

    private void showUploading(boolean uploading) {
        binding.publish.setEnabled(!uploading);
        binding.takePhoto.setEnabled(!uploading);
        binding.choosePhoto.setEnabled(!uploading);
        binding.uploadProgress.setVisibility(uploading ? View.VISIBLE : View.GONE);
    }

    private void showSelectedPhoto(Uri selectedPhoto) {
        photo = selectedPhoto;
        binding.preview.setImageURI(selectedPhoto);
        binding.uploadLabel.setVisibility(View.GONE);
        binding.uploadUrl.setText(null);
        binding.uploadUrl.setVisibility(View.GONE);
    }
}
