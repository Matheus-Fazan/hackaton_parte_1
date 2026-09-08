package com.prova.hackaton_parte_1.data;

import com.prova.hackaton_parte_1.data.model.Comment;
import com.prova.hackaton_parte_1.data.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class InMemoryFeedRepository implements FeedRepository {
    private final List<Post> posts = new ArrayList<>();
    private final Map<String, List<Comment>> comments = new HashMap<>();
    private final List<Listener<List<Post>>> postListeners = new ArrayList<>();
    private final Map<String, List<Listener<List<Comment>>>> commentListeners = new HashMap<>();

    @Override public Subscription observePosts(Listener<List<Post>> listener, Listener<Exception> errors) {
        postListeners.add(listener);
        listener.onData(sortedPosts());
        return () -> postListeners.remove(listener);
    }

    @Override public Subscription observeComments(String postId, Listener<List<Comment>> listener, Listener<Exception> errors) {
        commentListeners.computeIfAbsent(postId, id -> new ArrayList<>()).add(listener);
        listener.onData(new ArrayList<>(comments.getOrDefault(postId, Collections.emptyList())));
        return () -> commentListeners.getOrDefault(postId, Collections.emptyList()).remove(listener);
    }

    @Override public void createPost(Post post, Result result) {
        post.setId(UUID.randomUUID().toString());
        posts.add(post);
        emitPosts();
        result.done(null);
    }

    @Override public void react(String postId, boolean like, Result result) {
        for (Post post : posts) if (postId.equals(post.getId())) {
            if (like) post.setLikes(post.getLikes() + 1); else post.setDislikes(post.getDislikes() + 1);
            emitPosts();
            result.done(null);
            return;
        }
        result.done(new IllegalArgumentException("Publicação não encontrada."));
    }

    @Override public void addComment(String postId, Comment comment, Result result) {
        comments.computeIfAbsent(postId, id -> new ArrayList<>()).add(comment);
        for (Post post : posts) if (postId.equals(post.getId())) post.setComments(post.getComments() + 1);
        emitPosts();
        emitComments(postId);
        result.done(null);
    }

    @Override public boolean isRemote() { return false; }

    private List<Post> sortedPosts() {
        List<Post> copy = new ArrayList<>(posts);
        copy.sort(Comparator.comparingLong(Post::getCreatedAt).reversed());
        return copy;
    }

    private void emitPosts() { for (Listener<List<Post>> listener : new ArrayList<>(postListeners)) listener.onData(sortedPosts()); }
    private void emitComments(String postId) {
        List<Comment> current = new ArrayList<>(comments.getOrDefault(postId, Collections.emptyList()));
        for (Listener<List<Comment>> listener : new ArrayList<>(commentListeners.getOrDefault(postId, Collections.emptyList()))) listener.onData(current);
    }
}
