
package org.example.ui;

import org.example.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SupplyStockUI {
    public static void showStock() {
        StringBuilder sb = new StringBuilder();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            sb.append("=== Остатки древесины ===\n");
            ResultSet rs = stmt.executeQuery("SELECT * FROM wood");
            while (rs.next()) {
                sb.append("• ").append(rs.getString("type")).append(" (")
                  .append(rs.getString("supplier")).append("): ")
                  .append(rs.getInt("stock")).append("\n");
            }

            sb.append("\n=== Остатки сердцевин ===\n");
            rs = stmt.executeQuery("SELECT * FROM core");
            while (rs.next()) {
                sb.append("• ").append(rs.getString("type")).append(" (")
                  .append(rs.getString("magical_properties")).append("): ")
                  .append(rs.getInt("stock")).append("\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при получении остатков: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Склад компонентов", JOptionPane.INFORMATION_MESSAGE);
    }
}
