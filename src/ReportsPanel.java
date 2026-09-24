import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
public class ReportsPanel extends JPanel {
    private ProductDAO productDao = new ProductDAO();
    private SalesDAO salesDao = new SalesDAO();
    private JLabel totalSalesLabel, totalInventoryValueLabel, lowStockCountLabel;
    private DefaultTableModel bestSellerModel;
    private DefaultTableModel recentSalesModel;
    public ReportsPanel(){
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(buildSummaryPanel(), BorderLayout.NORTH);
        JPanel tables = new JPanel(new GridLayout(1, 2, 10, 0));
        tables.add(buildBestSellerTable());
        tables.add(buildRecentSalesTable());
        add(tables, BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Refresh Report");
        refreshBtn.addActionListener(e -> refresh());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);
        refresh();
    }
    private JPanel buildSummaryPanel(){
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Overview"));
        totalSalesLabel = new JLabel();
        totalInventoryValueLabel = new JLabel();
        lowStockCountLabel = new JLabel();
        for (JLabel l : new JLabel[]{totalSalesLabel, totalInventoryValueLabel, lowStockCountLabel}) {
            l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
            l.setHorizontalAlignment(SwingConstants.CENTER);
        }
        panel.add(wrapCard("Total Sales Revenue", totalSalesLabel));
        panel.add(wrapCard("Total Inventory Value", totalInventoryValueLabel));
        panel.add(wrapCard("Items Below Min Stock", lowStockCountLabel));
        return panel;
    }
    private JPanel wrapCard(String title, JLabel valueLabel){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        p.add(titleLabel, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(200, 60));
        return p;
    }
    private JScrollPane buildBestSellerTable(){
        bestSellerModel = new DefaultTableModel(new String[]{"Item", "Units Sold"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(bestSellerModel);
        JScrollPane scroll = new JScrollPane(t);
        scroll.setBorder(BorderFactory.createTitledBorder("Best Selling Items"));
        return scroll;
    }
    private JScrollPane buildRecentSalesTable(){
        recentSalesModel = new DefaultTableModel(new String[]{"Item", "Qty", "Total (Rs)", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(recentSalesModel);
        JScrollPane scroll = new JScrollPane(t);
        scroll.setBorder(BorderFactory.createTitledBorder("Recent Sales"));
        return scroll;
    }
    public void refresh(){
        double totalSales = salesDao.getTotalSalesValue();
        totalSalesLabel.setText("Rs. " + String.format("%.2f", totalSales));
        List<Product> products = productDao.getAllProducts();
        double inventoryValue = 0;
        int lowStockCount = 0;
        for (Product p : products){
            inventoryValue += p.getPrice() * p.getStock();
            if (p.isLowStock()) lowStockCount++;
        }
        totalInventoryValueLabel.setText("Rs. " + String.format("%.2f", inventoryValue));
        lowStockCountLabel.setText(String.valueOf(lowStockCount));
        if (lowStockCount > 0) lowStockCountLabel.setForeground(new Color(200, 0, 0));
        else lowStockCountLabel.setForeground(new Color(0, 130, 0));
        bestSellerModel.setRowCount(0);
        for (Object[] row : salesDao.getBestSellers(10)){
            bestSellerModel.addRow(row);
        }
        recentSalesModel.setRowCount(0);
        for (Object[] row : salesDao.getRecentSales(15)){
            recentSalesModel.addRow(new Object[]{row[0], row[1], String.format("%.2f", (double) row[2]), row[3]});
        }
    }
}
