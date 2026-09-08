package com.prova.hackaton_parte_1.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.prova.hackaton_parte_1.data.FeedRepository;
import com.prova.hackaton_parte_1.data.FeedRepositoryFactory;
import com.prova.hackaton_parte_1.data.model.Post;

import java.util.List;

public final class FeedViewModel extends AndroidViewModel {
    private final FeedRepository repository;
    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FeedRepository.Subscription subscription;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        repository = FeedRepositoryFactory.get(application);
        subscription = repository.observePosts(posts::setValue, e -> error.setValue(e.getMessage()));
    }

    LiveData<List<Post>> posts() { return posts; }
    LiveData<String> error() { return error; }
    void react(Post post, boolean like) { repository.react(post.getId(), like, e -> { if (e != null) error.setValue(e.getMessage()); }); }

    @Override protected void onCleared() { subscription.cancel(); }
}
