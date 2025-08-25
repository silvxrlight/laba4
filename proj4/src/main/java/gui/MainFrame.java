
package gui;

import com.mycompany.laba4.DatabaseManager;
import javax.swing.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private final DatabaseManager dbManager;
    
    public MainFrame(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Магазин волшебных палочек Олливандеры");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Палочки", new WandsPanel(dbManager));
        tabbedPane.addTab("Покупатели", new WizardsPanel(dbManager));
        tabbedPane.addTab("Компоненты", new ComponentsPanel(dbManager));
        tabbedPane.addTab("Продажи", new SalesPanel(dbManager));
        tabbedPane.addTab("Поставки", new DeliveryPanel(dbManager));
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        
        JMenuItem clearDataItem = new JMenuItem("Очистить все данные");
        clearDataItem.addActionListener(e -> clearAllData());
        
        fileMenu.add(clearDataItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        add(tabbedPane);
    }
    
    private void clearAllData() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Вы уверены, что хотите очистить все данные?",
            "Подтверждение",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.clearAllData();
                JOptionPane.showMessageDialog(
                    this,
                    "Все данные успешно очищены",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Ошибка при очистке данных: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
