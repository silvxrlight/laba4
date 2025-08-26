package org.example.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UIUtils {

    public static void installNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 14));
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static GridBagConstraints gbc() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.gridx = 0;
        gc.gridy = 0;
        return gc;
    }

    public static void addRow(JPanel panel, GridBagConstraints gc, String label, JComponent field) {
        gc.gridx = 0; panel.add(new JLabel(label), gc);
        gc.gridx = 1; panel.add(field, gc);
        gc.gridx = 0; gc.gridy++;
    }

    public static JPanel titled(JComponent c, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title, TitledBorder.LEADING, TitledBorder.TOP));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    public static JTable findFirstTable(Container root) {
        for (Component comp : root.getComponents()) {
            if (comp instanceof JTable) return (JTable) comp;
            if (comp instanceof Container) {
                JTable inner = findFirstTable((Container) comp);
                if (inner != null) return inner;
            }
        }
        return null;
    }

    public static List<JTable> findAllTables(Container root) {
        List<JTable> res = new ArrayList<>();
        collectTables(root, res);
        return res;
    }

    private static void collectTables(Container c, List<JTable> acc) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JTable) acc.add((JTable) comp);
            if (comp instanceof Container) collectTables((Container) comp, acc);
        }
    }
}
