package org.example.ui;

import org.example.DatabaseInitializer;
import org.example.DatabaseManager;
import org.example.WandInventoryApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final DatabaseManager db = new DatabaseManager();

    public MainFrame() {
        super("Магазин волшебных палочек Ollivanders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Палочки", createWandsTab());
        tabs.addTab("Поставки", createSupplyTab());
        tabs.addTab("Склад", createStockTab());
        setContentPane(tabs);
    }

    private JPanel createWandsTab() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(12,12,12,12));

        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        JButton create = new JButton("Создать");
        JButton sell = new JButton("Продать");
        JButton refresh = new JButton("Обновить");
        JButton clearAll = new JButton("Очистить всё");

        create.addActionListener(e -> {
            new WandInventoryApp().createWandPublic();
            refreshWands(root);
        });
        sell.addActionListener(e -> {
            new WandInventoryApp().sellWandPublic(root);
            refreshWands(root);
        });
        refresh.addActionListener(e -> refreshWands(root));
        clearAll.addActionListener(e -> {
            if (UIUtils.confirm(this, "Удалить все данные? Это действие нельзя отменить.")) {
                DatabaseInitializer.clearAllData();
                refreshAll();
            }
        });

        bar.add(create); bar.add(sell); bar.addSeparator();
        bar.add(refresh); bar.addSeparator(); bar.add(clearAll);

        JTable table = new JTable(db.getWandsTableModel());
        JScrollPane sp = new JScrollPane(table);

        root.add(bar, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);

        return root;
    }

    private JPanel createSupplyTab() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(12,12,12,12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = UIUtils.gbc();
        JTextField supplier = new JTextField(12);
        JComboBox<String> type = new JComboBox<>(new String[]{"WOOD", "CORE"});
        JTextField name = new JTextField(12);
        JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JButton add = new JButton("Добавить поставку");
        JButton refresh = new JButton("Обновить");

        UIUtils.addRow(form, gc, "Поставщик:", supplier);
        UIUtils.addRow(form, gc, "Тип:", type);
        UIUtils.addRow(form, gc, "Наименование:", name);
        UIUtils.addRow(form, gc, "Количество:", qty);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add); buttons.add(refresh);
        gc.gridx = 0; gc.gridy++; gc.gridwidth = 2; gc.anchor = GridBagConstraints.WEST;
        form.add(buttons, gc);

        add.addActionListener(e -> {
            try {
                String t = (String) type.getSelectedItem();
                String n = name.getText().trim();
                String s = supplier.getText().trim();
                int q = (int) qty.getValue();
                if (n.isEmpty() || s.isEmpty()) {
                    UIUtils.error(this, "Заполните все поля.");
                    return;
                }
                db.addSupply(s, t, n, q);
                UIUtils.info(this, "Поставка сохранена.");
                refreshAll();
                name.setText("");
                qty.setValue(1);
            } catch (Exception ex) {
                UIUtils.error(this, "Ошибка: " + ex.getMessage());
            }
        });
        refresh.addActionListener(e -> refreshAll());

        JTable table = new JTable(db.getSuppliesTableModel());
        JScrollPane sp = new JScrollPane(table);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, sp);
        split.setResizeWeight(0.3);

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel createStockTab() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(12,12,12,12));

        JTable wood = new JTable(db.getWoodTableModel());
        JTable core = new JTable(db.getCoreTableModel());

        JPanel left = UIUtils.titled(new JScrollPane(wood), "Древесина");
        JPanel right = UIUtils.titled(new JScrollPane(core), "Сердцевины");

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private void refreshWands(JPanel wandsTab) {
        JTable table = UIUtils.findFirstTable(wandsTab);
        if (table != null) table.setModel(db.getWandsTableModel());
    }

    private void refreshAll() {
        db.refreshCaches();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Палочки", createWandsTab());
        tabs.addTab("Поставки", createSupplyTab());
        tabs.addTab("Склад", createStockTab());
        setContentPane(tabs);
        revalidate();
        repaint();
    }

    public static void launch() {
        UIUtils.installNimbus();
        SwingUtilities.invokeLater(() -> {
            MainFrame mf = new MainFrame();
            mf.setVisible(true);
        });
    }
}
