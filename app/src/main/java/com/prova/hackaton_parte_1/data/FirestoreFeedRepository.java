package com.prova.hackaton_parte_1.data;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.prova.hackaton_parte_1.data.model.Comment;
import com.prova.hackaton_parte_1.data.model.Post;

import java.util.Collections;

final class FirestoreFeedRepository implements FeedRepository {
    private final CollectionReference posts = FirebaseFirestore.getInstance().collection("posts");

    @Override public Subscription observePosts(Listener<java.util.List<Post>> listener, Listener<Exception> errors) {
        ListenerRegistration registration = posts.orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) { errors.onData(error); return; }
                    listener.onData(snapshot == null ? Collections.emptyList() : snapshot.toObjects(Post.class));
                });
        return registration::remove;
    }

    @Override public Subscription observeComments(String postId, Listener<java.util.List<Comment>> listener, Listener<Exception> errors) {
        ListenerRegistration registration = posts.document(postId).collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) { errors.onData(error); return; }
                    listener.onData(snapshot == null ? Collections.emptyList() : snapshot.toObjects(Comment.class));
                });
        return registration::remove;
    }

    @Override public void createPost(Post post, Result result) {
        posts.add(post).addOnSuccessListener(id -> result.done(null)).addOnFailureListener(result::done);
    }

    @Override public void react(String postId, boolean like, Result result) {
        posts.document(postId).update(like ? "likes" : "dislikes", FieldValue.increment(1))
                .addOnSuccessListener(value -> result.done(null)).addOnFailureListener(result::done);
    }

    @Override public void addComment(String postId, Comment comment, Result result) {
        posts.document(postId).collection("comments").add(comment)
                .continueWithTask(task -> posts.document(postId)
                        .update("comments", FieldValue.increment(1)))
                .addOnSuccessListener(value -> result.done(null)).addOnFailureListener(result::done);
    }

    @Override public boolean isRemote() { return true; }
}
