package com.prova.hackaton_parte_1.ui.comments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prova.hackaton_parte_1.data.model.Comment;
import com.prova.hackaton_parte_1.databinding.ItemCommentBinding;

import java.util.ArrayList;
import java.util.List;

final class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Holder> {
    private final List<Comment> comments = new ArrayList<>();
    void submit(List<Comment> items) { comments.clear(); comments.addAll(items); notifyDataSetChanged(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) { return new Holder(ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)); }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.bind(comments.get(position)); }
    @Override public int getItemCount() { return comments.size(); }
    static final class Holder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;
        Holder(ItemCommentBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(Comment comment) { binding.author.setText(comment.getAuthor()); binding.text.setText(comment.getText()); }
    }
}
