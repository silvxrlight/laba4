
package org.example;

import org.example.ui.SupplyStockUI;
import org.example.ui.SupplyUI;
import org.example.ui.SupplyViewUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WandInventoryApp {

    public void createWandPublic() { createWand(); }
    public void sellWandPublic(JPanel wandsTab) {
        JTable table = org.example.ui.UIUtils.findFirstTable(wandsTab);
        if (table == null) {
            JOptionPane.showMessageDialog(null, "Таблица палочек не найдена.");
            return;
        }

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Выберите палочку в таблице.");
            return;
        }

        int wandId = (int) table.getValueAt(row, 0); // ID из первого столбца
        String customer = JOptionPane.showInputDialog("Введите имя покупателя:");
        if (customer == null || customer.isBlank()) return;

        try {
            DatabaseManager db = new DatabaseManager();
            db.sellWand(wandId, customer);
            JOptionPane.showMessageDialog(null, "Палочка продана: " + customer);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Ошибка при продаже: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    static List<Wood> woods = new ArrayList<>();
    static List<Core> cores = new ArrayList<>();
    static List<Wand> wands = new ArrayList<>();
    static List<Customer> customers = new ArrayList<>();

    static int wandId = 1;
    static int customerId = 1;

    public void launch() {
        DatabaseInitializer.initialize();
        loadAllFromDatabase();
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void loadAllFromDatabase() {
        try (var conn = DatabaseConnection.getConnection()) {
            var rsWood = conn.createStatement().executeQuery("SELECT * FROM wood");
            while (rsWood.next()) {
                woods.add(new Wood(rsWood.getInt("id"), rsWood.getString("type"), rsWood.getString("supplier")));
            }

            var rsCore = conn.createStatement().executeQuery("SELECT * FROM core");
            while (rsCore.next()) {
                cores.add(new Core(rsCore.getInt("id"), rsCore.getString("type"), rsCore.getString("magical_properties")));
            }

            var rsCustomer = conn.createStatement().executeQuery("SELECT * FROM customer");
            while (rsCustomer.next()) {
                customers.add(new Customer(
                        rsCustomer.getInt("id"),
                        rsCustomer.getString("name"),
                        java.sql.Date.valueOf(rsCustomer.getString("purchase_date"))
                ));
            }

            var rsWand = conn.createStatement().executeQuery("SELECT * FROM wand");
            while (rsWand.next()) {
                int woodId = rsWand.getInt("wood_id");
                int coreId = rsWand.getInt("core_id");
                int customerId = rsWand.getInt("customer_id");

                Wood wood = woods.stream().filter(w -> w.id == woodId).findFirst().orElse(null);
                Core core = cores.stream().filter(c -> c.id == coreId).findFirst().orElse(null);
                Customer customer = customers.stream().filter(c -> c.id == customerId).findFirst().orElse(null);

                Wand wand = new Wand(
                        rsWand.getInt("id"),
                        java.sql.Date.valueOf(rsWand.getString("creation_date")),
                        rsWand.getBoolean("in_stock"),
                        wood,
                        core
                );
                wand.owner = customer;
                wands.add(wand);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке данных из базы: " + e.getMessage());
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Учёт волшебных палочек - Олливандерс");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton createWandButton = new JButton("Создать палочку");
        createWandButton.addActionListener(e -> createWand());

        JButton sellWandButton = new JButton("Продать палочку");
        sellWandButton.addActionListener(e -> sellWand());

        JButton viewWandsButton = new JButton("Показать все палочки");
        viewWandsButton.addActionListener(e -> viewWands());

        JButton stockButton = new JButton("Палочки на складе");
        stockButton.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Wand wand : wands) {
                if (wand.inStock) {
                    sb.append(wand).append("\n");
                }
            }
            JOptionPane.showMessageDialog(null, sb.length() > 0 ? sb.toString() : "На складе нет палочек.");
        });

        JButton supplyButton = new JButton("Добавить поставку");
        supplyButton.addActionListener(e -> SupplyUI.showSupplyDialog());

        JButton viewSupplyButton = new JButton("Просмотреть поставки");
        viewSupplyButton.addActionListener(e -> SupplyViewUI.showSupplies());

        JButton clearDataButton = new JButton("Очистить все данные");
        clearDataButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Удалить все данные? Это действие необратимо.", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseInitializer.clearAllData();
                woods.clear();
                cores.clear();
                wands.clear();
                customers.clear();
                JOptionPane.showMessageDialog(null, "Все данные удалены.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(createWandButton);
        panel.add(sellWandButton);
        panel.add(viewWandsButton);
        panel.add(stockButton);
        panel.add(supplyButton);
        panel.add(viewSupplyButton);
        panel.add(clearDataButton);

        JButton viewStockButton = new JButton("Склад компонентов");
        viewStockButton.addActionListener(e -> SupplyStockUI.showStock());
        panel.add(viewStockButton);


        frame.getContentPane().add(panel);
        frame.setSize(750, 200);
        frame.setVisible(true);
    }


    public static void reloadComponents() {
        woods.clear();
        cores.clear();
        try (var conn = DatabaseConnection.getConnection()) {
            var rsWood = conn.createStatement().executeQuery("SELECT * FROM wood");
            while (rsWood.next()) {
                woods.add(new Wood(rsWood.getInt("id"), rsWood.getString("type"), rsWood.getString("supplier")));
            }

            var rsCore = conn.createStatement().executeQuery("SELECT * FROM core");
            while (rsCore.next()) {
                cores.add(new Core(rsCore.getInt("id"), rsCore.getString("type"), rsCore.getString("magical_properties")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при обновлении компонентов: " + e.getMessage());
        }
    }



    private void createWand() {
        List<Wood> availableWoods = new ArrayList<>();
        List<Core> availableCores = new ArrayList<>();

        try (var conn = DatabaseConnection.getConnection()) {
            var rsWoods = conn.createStatement().executeQuery("SELECT * FROM wood WHERE stock > 0");
            while (rsWoods.next()) {
                availableWoods.add(new Wood(
                        rsWoods.getInt("id"),
                        rsWoods.getString("type"),
                        rsWoods.getString("supplier")
                ));
            }

            var rsCores = conn.createStatement().executeQuery("SELECT * FROM core WHERE stock > 0");
            while (rsCores.next()) {
                availableCores.add(new Core(
                        rsCores.getInt("id"),
                        rsCores.getString("type"),
                        rsCores.getString("magical_properties")
                ));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке компонентов: " + e.getMessage());
            return;
        }

        if (availableWoods.isEmpty() || availableCores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет доступных компонентов на складе.");
            return;
        }

        Wood wood = (Wood) JOptionPane.showInputDialog(null, "Выберите древесину:", "Создание палочки",
                JOptionPane.QUESTION_MESSAGE, null, availableWoods.toArray(), null);
        Core core = (Core) JOptionPane.showInputDialog(null, "Выберите сердцевину:", "Создание палочки",
                JOptionPane.QUESTION_MESSAGE, null, availableCores.toArray(), null);

        if (wood == null || core == null) return;

        try (var conn = DatabaseConnection.getConnection()) {
            var update1 = conn.prepareStatement("UPDATE wood SET stock = stock - 1 WHERE id = ?");
            update1.setInt(1, wood.id);
            update1.executeUpdate();

            var update2 = conn.prepareStatement("UPDATE core SET stock = stock - 1 WHERE id = ?");
            update2.setInt(1, core.id);
            update2.executeUpdate();

            Wand wand = new Wand(wandId++, new Date(), true, wood, core);
            wands.add(wand);
            var insert = conn.prepareStatement("INSERT INTO wand (creation_date, in_stock, wood_id, core_id, customer_id) VALUES (?, ?, ?, ?, ?)");
            insert.setString(1, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            insert.setBoolean(2, true);
            insert.setInt(3, wood.id);
            insert.setInt(4, core.id);
            insert.setNull(5, java.sql.Types.INTEGER);
            insert.executeUpdate();
            JOptionPane.showMessageDialog(null, "Создана палочка: " + wand);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при создании палочки: " + e.getMessage());
        }
    }

    private void sellWand() {
        List<Wand> available = new ArrayList<>();
        for (Wand wand : wands) {
            if (wand.inStock) available.add(wand);
        }
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет доступных палочек.");
            return;
        }

        Wand wand = (Wand) JOptionPane.showInputDialog(null, "Выберите палочку для продажи:", "Продажа палочки",
                JOptionPane.QUESTION_MESSAGE, null, available.toArray(), null);
        if (wand == null) return;

        String name = JOptionPane.showInputDialog("Введите имя покупателя:");
        if (name == null || name.isEmpty()) return;

        Customer customer = new Customer(0, name, new Date());

        try (var conn = DatabaseConnection.getConnection()) {
            var insert = conn.prepareStatement(
                    "INSERT INTO customer (name, purchase_date) VALUES (?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            insert.setString(1, customer.name);
            insert.setString(2, new java.text.SimpleDateFormat("yyyy-MM-dd").format(customer.purchaseDate));
            insert.executeUpdate();

            var keys = insert.getGeneratedKeys();
            if (keys.next()) {
                customer.id = keys.getInt(1);
            }

            customers.add(customer);
            wand.sellTo(customer);

            var update = conn.prepareStatement("UPDATE wand SET in_stock = false, customer_id = ? WHERE id = ?");
            update.setInt(1, customer.id);
            update.setInt(2, wand.id);
            update.executeUpdate();

            JOptionPane.showMessageDialog(null, "Палочка продана: " + customer.name);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Ошибка при сохранении продажи: " + ex.getMessage());
        }
    }


    private void viewWands() {
        StringBuilder sb = new StringBuilder();
        for (Wand wand : wands) {
            sb.append(wand).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.length() > 0 ? sb.toString() : "Нет палочек.");
    }
}
