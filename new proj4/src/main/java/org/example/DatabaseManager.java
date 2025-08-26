package org.example;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseManager {

    public DefaultTableModel getWandsTableModel() {
        String[] cols = {"ID", "Дата", "В наличии", "Древесина", "Сердцевина", "Покупатель"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(
                    "SELECT w.id, w.creation_date, w.in_stock, wd.type AS wood, c.type AS core, cu.name AS customer " +
                            "FROM wand w " +
                            "LEFT JOIN wood wd ON wd.id = w.wood_id " +
                            "LEFT JOIN core c ON c.id = w.core_id " +
                            "LEFT JOIN customer cu ON cu.id = w.customer_id " +
                            "ORDER BY w.id DESC"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("creation_date"),
                        rs.getBoolean("in_stock") ? "Да" : "Нет",
                        rs.getString("wood"),
                        rs.getString("core"),
                        rs.getString("customer")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }

    public DefaultTableModel getWoodTableModel() {
        String[] cols = {"ID", "Тип", "Поставщик", "Остаток"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT id, type, supplier, stock FROM wood ORDER BY type");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("type"), rs.getString("supplier"), rs.getInt("stock")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }

    public DefaultTableModel getCoreTableModel() {
        String[] cols = {"ID", "Тип", "Свойства", "Остаток"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT id, type, magical_properties, stock FROM core ORDER BY type");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("type"), rs.getString("magical_properties"), rs.getInt("stock")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }

    public DefaultTableModel getSuppliesTableModel() {
        String[] cols = {"Поставка", "Дата", "Поставщик", "Тип", "Наименование", "Кол-во"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(
                    "SELECT s.id, s.date, s.supplier, sc.type, sc.component_name, sc.quantity " +
                            "FROM supply s JOIN supply_component sc ON s.id = sc.supply_id ORDER BY s.id DESC"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("supplier"),
                        rs.getString("type"),
                        rs.getString("component_name"),
                        rs.getInt("quantity")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }

    public void addSupply(String supplier, String type, String name, int quantity) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                var insertSupply = conn.prepareStatement("INSERT INTO supply (date, supplier) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                var sdf = new SimpleDateFormat("yyyy-MM-dd");
                insertSupply.setString(1, sdf.format(new Date()));
                insertSupply.setString(2, supplier);
                insertSupply.executeUpdate();
                var rs = insertSupply.getGeneratedKeys();
                int supplyId = -1;
                if (rs.next()) supplyId = rs.getInt(1);

                var insertComp = conn.prepareStatement("INSERT INTO supply_component (type, component_name, quantity, supply_id) VALUES (?, ?, ?, ?)");
                insertComp.setString(1, type);
                insertComp.setString(2, name);
                insertComp.setInt(3, quantity);
                insertComp.setInt(4, supplyId);
                insertComp.executeUpdate();

                if ("WOOD".equalsIgnoreCase(type)) {
                    var check = conn.prepareStatement("SELECT id FROM wood WHERE type = ?");
                    check.setString(1, name);
                    var r = check.executeQuery();
                    if (r.next()) {
                        var upd = conn.prepareStatement("UPDATE wood SET stock = stock + ? WHERE type = ?");
                        upd.setInt(1, quantity);
                        upd.setString(2, name);
                        upd.executeUpdate();
                    } else {
                        var ins = conn.prepareStatement("INSERT INTO wood (type, supplier, stock) VALUES (?, ?, ?)");
                        ins.setString(1, name);
                        ins.setString(2, supplier);
                        ins.setInt(3, quantity);
                        ins.executeUpdate();
                    }
                } else {
                    var check = conn.prepareStatement("SELECT id FROM core WHERE type = ?");
                    check.setString(1, name);
                    var r = check.executeQuery();
                    if (r.next()) {
                        var upd = conn.prepareStatement("UPDATE core SET stock = stock + ? WHERE type = ?");
                        upd.setInt(1, quantity);
                        upd.setString(2, name);
                        upd.executeUpdate();
                    } else {
                        var ins = conn.prepareStatement("INSERT INTO core (type, magical_properties, stock) VALUES (?, ?, ?)");
                        ins.setString(1, name);
                        ins.setString(2, "");
                        ins.setInt(3, quantity);
                        ins.executeUpdate();
                    }
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void refreshCaches() {

    }

    public void sellWand(int wandId, String customerName) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int customerId = -1;

                // ищем покупателя
                var checkCustomer = conn.prepareStatement("SELECT id FROM customer WHERE name = ?");
                checkCustomer.setString(1, customerName);
                var rs = checkCustomer.executeQuery();
                if (rs.next()) {
                    customerId = rs.getInt(1);
                } else {
                    // создаём нового покупателя
                    var insCustomer = conn.prepareStatement(
                            "INSERT INTO customer (name, purchase_date) VALUES (?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    insCustomer.setString(1, customerName);
                    insCustomer.setString(2, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    insCustomer.executeUpdate();

                    var genKeys = insCustomer.getGeneratedKeys();
                    if (genKeys.next()) customerId = genKeys.getInt(1);
                }

                // обновляем палочку: снимаем "в наличии" и ставим покупателя
                var update = conn.prepareStatement(
                        "UPDATE wand SET in_stock = false, customer_id = ? WHERE id = ?"
                );
                update.setInt(1, customerId);
                update.setInt(2, wandId);
                update.executeUpdate();

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

}
