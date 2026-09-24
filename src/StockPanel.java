import javax.swing.*;
import java.awt.*;
import java.util.List;
public class StockPanel extends JPanel{
    private ProductDAO dao = new ProductDAO();
    private JComboBox<Product> productBox;
    private JTextField qtyField;
    private JLabel currentStockLabel;
    private JLabel alertLabel;
    public StockPanel(){
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Log Incoming / Outgoing Stock"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        productBox = new JComboBox<>();
        loadProducts();
        productBox.addActionListener(e -> updateCurrentStockLabel());
        qtyField = new JTextField(8);
        currentStockLabel = new JLabel("--");
        alertLabel = new JLabel(" ");
        alertLabel.setForeground(new Color(200, 0, 0));
        alertLabel.setFont(alertLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1; form.add(productBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Current Stock:"), gbc);
        gbc.gridx = 1; form.add(currentStockLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; form.add(qtyField, gbc);
        JButton restockBtn = new JButton("Add Stock (Restock)");
        JButton removeBtn = new JButton("Remove Stock (Damaged/Adjust)");
        restockBtn.addActionListener(e -> doStockChange(true));
        removeBtn.addActionListener(e -> doStockChange(false));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.add(restockBtn);
        btnRow.add(removeBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btnRow, gbc);
        gbc.gridy = 4;
        form.add(alertLabel, gbc);
        add(form, BorderLayout.NORTH);
        updateCurrentStockLabel();
    }
    private void loadProducts(){
        productBox.removeAllItems();
        List<Product> products = dao.getAllProducts();
        for (Product p : products) productBox.addItem(p);
    }
    private void updateCurrentStockLabel(){
        Product p = (Product) productBox.getSelectedItem();
        if (p != null){
            currentStockLabel.setText(String.valueOf(p.getStock()));
        } else{
            currentStockLabel.setText("--");
        }
        alertLabel.setText(" ");
    }
    private void doStockChange(boolean isAdd){
        Product p = (Product) productBox.getSelectedItem();
        if (p == null){
            JOptionPane.showMessageDialog(this, "Add a product first from the Products tab.");
            return;
        }
        try{
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0){
                JOptionPane.showMessageDialog(this, "Enter a quantity greater than 0.");
                return;
            }
            if (!isAdd && qty > p.getStock()){
                JOptionPane.showMessageDialog(this, "Can't remove more than current stock.");
                return;
            }
            int change = isAdd ? qty : -qty;
            dao.updateStock(p.getId(), change, isAdd ? "IN" : "OUT");
            loadProducts();
            for (int i = 0; i < productBox.getItemCount(); i++){
                if (productBox.getItemAt(i).getId() == p.getId()){
                    productBox.setSelectedIndex(i);
                    break;
                }
            }
            Product updated = dao.getProductById(p.getId());
            currentStockLabel.setText(String.valueOf(updated.getStock()));
            qtyField.setText("");
            if (updated.isLowStock()){
                alertLabel.setText("⚠ LOW STOCK WARNING: " + updated.getName() + " is at " + updated.getStock() + " (min " + updated.getMinStock() + ")");
            } else{
                alertLabel.setText(" ");
            }
        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
        }
    }
    public void refresh(){
        Product selected = (Product) productBox.getSelectedItem();
        loadProducts();
        if (selected != null){
            for (int i = 0; i < productBox.getItemCount(); i++){
                if (productBox.getItemAt(i).getId() == selected.getId()){
                    productBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        updateCurrentStockLabel();
    }
}
