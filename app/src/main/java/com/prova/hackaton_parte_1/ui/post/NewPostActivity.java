package com.prova.hackaton_parte_1.ui.post;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.prova.hackaton_parte_1.R;
import com.prova.hackaton_parte_1.data.CloudinaryUploader;
import com.prova.hackaton_parte_1.data.FeedRepository;
import com.prova.hackaton_parte_1.data.FeedRepositoryFactory;
import com.prova.hackaton_parte_1.data.model.Post;
import com.prova.hackaton_parte_1.databinding.ActivityNewPostBinding;

import java.io.File;

public final class NewPostActivity extends AppCompatActivity {
    private ActivityNewPostBinding binding;
    private Uri photo;
    private ActivityResultLauncher<Uri> camera;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationIcon(android.R.drawable.ic_media_previous);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        camera = registerForActivityResult(new ActivityResultContracts.TakePicture(), ok -> { if (ok) binding.preview.setImageURI(photo); });
        ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) { photo = uri; binding.preview.setImageURI(uri); }
        });
        binding.takePhoto.setOnClickListener(v -> {
            File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (folder == null) { Snackbar.make(binding.getRoot(), R.string.error_photo, Snackbar.LENGTH_LONG).show(); return; }
            photo = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(folder, "post_" + System.currentTimeMillis() + ".jpg"));
            camera.launch(photo);
        });
        binding.choosePhoto.setOnClickListener(v -> gallery.launch("image/*"));
        binding.publish.setOnClickListener(v -> publish());
    }

    private void publish() {
        if (photo == null) { Snackbar.make(binding.getRoot(), R.string.error_photo, Snackbar.LENGTH_LONG).show(); return; }
        binding.publish.setEnabled(false);
        FeedRepository repository = FeedRepositoryFactory.get(this);
        String caption = String.valueOf(binding.caption.getText()).trim();
        if (!repository.isRemote()) {
            save(repository, photo.toString(), caption);
            return;
        }
        if (!CloudinaryUploader.isConfigured(this)) {
            binding.publish.setEnabled(true);
            Snackbar.make(binding.getRoot(), "Configure o Cloudinary antes de publicar no feed compartilhado.", Snackbar.LENGTH_LONG).show();
            return;
        }
        CloudinaryUploader.upload(this, photo, (url, error) -> {
            if (error != null) {
                binding.publish.setEnabled(true);
                Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            } else save(repository, url, caption);
        });
    }

    private void save(FeedRepository repository, String imageUrl, String caption) {
        repository.createPost(new Post(imageUrl, caption), error -> {
            if (error == null) finish();
            else {
                binding.publish.setEnabled(true);
                Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
