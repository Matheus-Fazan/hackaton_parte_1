package com.prova.hackaton_parte_1.ui.main;

import static org.junit.Assert.assertEquals;

import com.prova.hackaton_parte_1.data.model.WelcomeMessage;

import org.junit.Test;

public class MainViewModelTest {

    @Test
    public void exposesInitialMessage() {
        WelcomeMessage message = new MainViewModel().getMessage().getValue();

        assertEquals("Hello World!", message.getText());
    }
}
