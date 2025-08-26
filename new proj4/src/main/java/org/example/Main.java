package org.example;

import org.example.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        MainFrame.launch();
    }
}
