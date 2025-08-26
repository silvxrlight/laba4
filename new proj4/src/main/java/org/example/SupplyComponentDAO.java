
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SupplyComponentDAO {
    public static void insertComponent(SupplyComponent.ComponentType type, String name, int quantity, int supplyId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO supply_component (type, component_name, quantity, supply_id) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, type.toString());
            ps.setString(2, name);
            ps.setInt(3, quantity);
            ps.setInt(4, supplyId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
