
package org.example;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // Миграции: добавление поля stock, если его нет
            try { stmt.executeUpdate("ALTER TABLE wood ADD COLUMN stock INTEGER DEFAULT 0"); } catch (Exception ignored) {}
            try { stmt.executeUpdate("ALTER TABLE core ADD COLUMN stock INTEGER DEFAULT 0"); } catch (Exception ignored) {}

            // Создание таблиц
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS wood (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, supplier TEXT, stock INTEGER DEFAULT 0)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS core (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, magical_properties TEXT, stock INTEGER DEFAULT 0)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS customer (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, purchase_date TEXT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS wand (id INTEGER PRIMARY KEY AUTOINCREMENT, creation_date TEXT, in_stock BOOLEAN, wood_id INTEGER, core_id INTEGER, customer_id INTEGER)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS supply (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, supplier TEXT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS supply_component (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, component_name TEXT, quantity INTEGER, supply_id INTEGER)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllData() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM wand");
            stmt.executeUpdate("DELETE FROM customer");
            stmt.executeUpdate("DELETE FROM wood");
            stmt.executeUpdate("DELETE FROM core");
            stmt.executeUpdate("DELETE FROM supply_component");
            stmt.executeUpdate("DELETE FROM supply");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
