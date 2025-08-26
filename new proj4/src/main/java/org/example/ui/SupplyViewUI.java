
package org.example.ui;

import org.example.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SupplyViewUI {
    public static void showSupplies() {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT s.id, s.date, s.supplier, sc.type, sc.component_name, sc.quantity " +
                "FROM supply s JOIN supply_component sc ON s.id = sc.supply_id ORDER BY s.id DESC"
            );
            int currentSupply = -1;
            while (rs.next()) {
                int supplyId = rs.getInt("id");
                if (supplyId != currentSupply) {
                    currentSupply = supplyId;
                    sb.append("\n--- Поставка №").append(supplyId).append(" от ").append(rs.getString("date"))
                      .append(" (").append(rs.getString("supplier")).append(") ---\n");
                }
                sb.append("• ").append(rs.getString("type")).append(": ").append(rs.getString("component_name"))
                  .append(" x").append(rs.getInt("quantity")).append("\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при получении поставок: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(null, sb.length() > 0 ? sb.toString() : "Нет данных о поставках.");
    }
}
