package com.prova.hackaton_parte_1.data;

import com.prova.hackaton_parte_1.data.model.Comment;
import com.prova.hackaton_parte_1.data.model.Post;

import java.util.List;

public interface FeedRepository {
    interface Listener<T> { void onData(T data); }
    interface Result { void done(Exception error); }
    interface Subscription { void cancel(); }

    Subscription observePosts(Listener<List<Post>> listener, Listener<Exception> errors);
    Subscription observeComments(String postId, Listener<List<Comment>> listener, Listener<Exception> errors);
    void createPost(Post post, Result result);
    void react(String postId, boolean like, Result result);
    void addComment(String postId, Comment comment, Result result);
    boolean isRemote();
}
