package com.prova.hackaton_parte_1.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<MainUiState> uiState = new MutableLiveData<>(
            new MainUiState("Hello World!")
    );

    public LiveData<MainUiState> getUiState() {
        return uiState;
    }
}
