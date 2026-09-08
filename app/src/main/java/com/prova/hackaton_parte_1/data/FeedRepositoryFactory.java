package com.prova.hackaton_parte_1.data;

import android.content.Context;

import com.google.firebase.FirebaseApp;

public final class FeedRepositoryFactory {
    private static FeedRepository repository;

    private FeedRepositoryFactory() { }

    public static synchronized FeedRepository get(Context context) {
        if (repository != null) return repository;
        FirebaseApp app = FirebaseApp.initializeApp(context.getApplicationContext());
        repository = app == null ? new InMemoryFeedRepository() : new FirestoreFeedRepository();
        return repository;
    }
}
