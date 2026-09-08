package com.prova.hackaton_parte_1.data;

import static org.junit.Assert.assertEquals;

import com.prova.hackaton_parte_1.data.model.Comment;
import com.prova.hackaton_parte_1.data.model.Post;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class InMemoryFeedRepositoryTest {
    @Test public void storesReactionsAndComments() {
        InMemoryFeedRepository repository = new InMemoryFeedRepository();
        AtomicReference<Post> published = new AtomicReference<>();
        repository.observePosts(posts -> published.set(posts.isEmpty() ? null : posts.get(0)), error -> { });
        repository.createPost(new Post("content://photo", "Legenda"), error -> { });
        repository.react(published.get().getId(), true, error -> { });
        repository.addComment(published.get().getId(), new Comment("Ana", "Legal"), error -> { });

        assertEquals(1, published.get().getLikes());
        assertEquals(1, published.get().getComments());
    }
}
