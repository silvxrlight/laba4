
package com.mycompany.laba4;

import gui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.initializeDatabase();
            try {
                dbManager.initializeDatabase();
            } catch (Exception e) {
                System.err.println("Ошибка при инициализации поставок: " + e.getMessage());
            }
            new MainFrame(dbManager).setVisible(true);
        });
    }
}