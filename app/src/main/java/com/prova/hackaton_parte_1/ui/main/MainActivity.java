package com.prova.hackaton_parte_1.ui.main;

import android.os.Bundle;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.prova.hackaton_parte_1.data.model.Post;
import com.prova.hackaton_parte_1.databinding.ActivityMainBinding;
import com.prova.hackaton_parte_1.ui.comments.CommentsActivity;
import com.prova.hackaton_parte_1.ui.post.NewPostActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FeedViewModel viewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        PostAdapter adapter = new PostAdapter(new PostAdapter.Actions() {
            @Override public void react(Post post, boolean like) { viewModel.react(post, like); }
            @Override public void comments(Post post) {
                startActivity(new Intent(MainActivity.this, CommentsActivity.class)
                        .putExtra(CommentsActivity.EXTRA_POST_ID, post.getId()));
            }
        });
        binding.posts.setLayoutManager(new LinearLayoutManager(this));
        binding.posts.setAdapter(adapter);
        binding.newPost.setOnClickListener(v -> startActivity(new Intent(this, NewPostActivity.class)));
        viewModel.posts().observe(this, posts -> {
            adapter.submit(posts);
            binding.empty.setVisibility(posts.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        });
        viewModel.error().observe(this, error -> { if (error != null) Snackbar.make(binding.main, error, Snackbar.LENGTH_LONG).show(); });

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
