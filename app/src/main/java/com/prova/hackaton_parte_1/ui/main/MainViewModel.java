package com.prova.hackaton_parte_1.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prova.hackaton_parte_1.data.model.WelcomeMessage;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<WelcomeMessage> message = new MutableLiveData<>(
            new WelcomeMessage("Hello World!")
    );

    public LiveData<WelcomeMessage> getMessage() {
        return message;
    }
}
