
package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseUtils {
    public static int getLastInsertId(String tableName) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS id FROM " + tableName);
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new Exception("Не удалось получить ID последней вставки");
    }
}
