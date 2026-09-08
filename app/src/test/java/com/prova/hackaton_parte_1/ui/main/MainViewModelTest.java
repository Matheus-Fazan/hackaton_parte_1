package com.prova.hackaton_parte_1.ui.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MainViewModelTest {

    @Test
    public void exposesInitialMessage() {
        MainUiState state = new MainViewModel().getUiState().getValue();

        assertEquals("Hello World!", state.getMessage());
    }
}
