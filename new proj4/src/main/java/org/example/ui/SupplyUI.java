
package org.example.ui;

import org.example.*;

import javax.swing.*;
import java.awt.*;

public class SupplyUI {
    public static void showSupplyDialog() {
        JTextField supplierField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JTextField quantityField = new JTextField(5);

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"WOOD", "CORE"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Поставщик:"));
        panel.add(supplierField);
        panel.add(new JLabel("Тип компонента:"));
        panel.add(typeBox);
        panel.add(new JLabel("Название компонента:"));
        panel.add(nameField);
        panel.add(new JLabel("Количество:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Добавить поставку", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String supplier = supplierField.getText();
                String type = (String) typeBox.getSelectedItem();
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());

                SupplyDAO.insertSupply(supplier);
                int supplyId = DatabaseUtils.getLastInsertId("supply");
                SupplyComponentDAO.insertComponent(SupplyComponent.ComponentType.valueOf(type), name, quantity, supplyId);

                if ("WOOD".equals(type)) {
                    try (var conn = DatabaseConnection.getConnection()) {
                        var check = conn.prepareStatement("SELECT id FROM wood WHERE type = ?");
                        check.setString(1, name);
                        var rs = check.executeQuery();
                        if (rs.next()) {
                            var update = conn.prepareStatement("UPDATE wood SET stock = stock + ? WHERE type = ?");
                            update.setInt(1, quantity);
                            update.setString(2, name);
                            update.executeUpdate();
                        } else {
                            var insert = conn.prepareStatement("INSERT INTO wood (type, supplier, stock) VALUES (?, ?, ?)");
                            insert.setString(1, name);
                            insert.setString(2, supplier);
                            insert.setInt(3, quantity);
                            insert.executeUpdate();
                        }
                    }
                } else {
                    try (var conn = DatabaseConnection.getConnection()) {
                        var check = conn.prepareStatement("SELECT id FROM core WHERE type = ?");
                        check.setString(1, name);
                        var rs = check.executeQuery();
                        if (rs.next()) {
                            var update = conn.prepareStatement("UPDATE core SET stock = stock + ? WHERE type = ?");
                            update.setInt(1, quantity);
                            update.setString(2, name);
                            update.executeUpdate();
                        } else {
                            var insert = conn.prepareStatement("INSERT INTO core (type, magical_properties, stock) VALUES (?, ?, ?)");
                            insert.setString(1, name);
                            insert.setString(2, "Imported via supply");
                            insert.setInt(3, quantity);
                            insert.executeUpdate();
                        }
                    }
                }

                WandInventoryApp.reloadComponents();
                JOptionPane.showMessageDialog(null, "Поставка добавлена успешно.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка: " + ex.getMessage());
            }
        }
    }
}
