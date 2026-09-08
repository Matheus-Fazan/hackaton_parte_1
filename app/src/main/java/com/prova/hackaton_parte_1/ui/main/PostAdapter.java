package com.prova.hackaton_parte_1.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prova.hackaton_parte_1.data.model.Post;
import com.prova.hackaton_parte_1.databinding.ItemPostBinding;
import com.prova.hackaton_parte_1.ui.common.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public final class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {
    interface Actions { void react(Post post, boolean like); void comments(Post post); }
    private final List<Post> posts = new ArrayList<>();
    private final Actions actions;

    PostAdapter(Actions actions) { this.actions = actions; }

    void submit(List<Post> items) {
        posts.clear();
        posts.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.bind(posts.get(position)); }
    @Override public int getItemCount() { return posts.size(); }

    final class Holder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;
        Holder(ItemPostBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(Post post) {
            ImageLoader.load(binding.photo, post.getImageUrl());
            binding.caption.setText(post.getCaption());
            binding.like.setText("Curtir " + post.getLikes());
            binding.dislike.setText("Não curtir " + post.getDislikes());
            binding.comment.setText("Comentar " + post.getComments());
            binding.like.setOnClickListener(v -> actions.react(post, true));
            binding.dislike.setOnClickListener(v -> actions.react(post, false));
            binding.comment.setOnClickListener(v -> actions.comments(post));
        }
    }
}
