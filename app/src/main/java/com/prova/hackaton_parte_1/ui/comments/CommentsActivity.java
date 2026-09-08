package com.prova.hackaton_parte_1.ui.comments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.prova.hackaton_parte_1.R;
import com.prova.hackaton_parte_1.data.FeedRepository;
import com.prova.hackaton_parte_1.data.FeedRepositoryFactory;
import com.prova.hackaton_parte_1.data.model.Comment;
import com.prova.hackaton_parte_1.databinding.ActivityCommentsBinding;

public final class CommentsActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "post_id";
    private FeedRepository.Subscription subscription;
    private ActivityCommentsBinding binding;
    private FeedRepository repository;
    private CommentAdapter adapter;
    private String postId;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId == null) { finish(); return; }
        binding.toolbar.setNavigationIcon(android.R.drawable.ic_media_previous);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        adapter = new CommentAdapter();
        binding.comments.setLayoutManager(new LinearLayoutManager(this));
        binding.comments.setAdapter(adapter);
        repository = FeedRepositoryFactory.get(this);
        binding.send.setOnClickListener(v -> {
            String text = String.valueOf(binding.comment.getText()).trim();
            if (text.isEmpty()) { binding.comment.setError(getString(R.string.error_comment)); return; }
            String author = String.valueOf(binding.author.getText()).trim();
            repository.addComment(postId, new Comment(author.isEmpty() ? getString(R.string.anonymous) : author, text), error -> {
                if (error == null) binding.comment.setText("");
                else Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG).show();
            });
        });
    }

    @Override protected void onStart() {
        super.onStart();
        if (repository == null) return;
        subscription = repository.observeComments(postId, comments -> {
            adapter.submit(comments);
            binding.empty.setVisibility(comments.isEmpty() ? View.VISIBLE : View.GONE);
        }, error -> Snackbar.make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG).show());
    }

    @Override protected void onStop() { super.onStop(); if (subscription != null) subscription.cancel(); }
}
