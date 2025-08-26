
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class SupplyDAO {
    public static void insertSupply(String supplier) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO supply (date, supplier) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            ps.setString(1, today);
            ps.setString(2, supplier);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createTables() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS supply (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, supplier TEXT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS supply_component (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, component_name TEXT, quantity INTEGER, supply_id INTEGER)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearSupplyTables() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM supply_component");
            stmt.executeUpdate("DELETE FROM supply");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
