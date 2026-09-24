import javax.swing.*;
public class MainWindow extends JFrame {
    private ProductPanel productPanel;
    private StockPanel stockPanel;
    private CheckoutPanel checkoutPanel;
    private ReportsPanel reportsPanel;
    public MainWindow(){
        setTitle("Smart Inventory & Order Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        JTabbedPane tabs = new JTabbedPane();
        productPanel = new ProductPanel();
        stockPanel = new StockPanel();
        checkoutPanel = new CheckoutPanel();
        reportsPanel = new ReportsPanel();
        tabs.addTab("Products", productPanel);
        tabs.addTab("Stock Entry", stockPanel);
        tabs.addTab("Checkout", checkoutPanel);
        tabs.addTab("Reports", reportsPanel);
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            switch (idx){
                case 0: productPanel.refreshTable(); break;
                case 1: stockPanel.refresh(); break;
                case 2: checkoutPanel.refresh(); break;
                case 3: reportsPanel.refresh(); break;
            }
        });
        add(tabs);
    }
}
