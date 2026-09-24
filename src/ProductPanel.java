import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
public class ProductPanel extends JPanel {
    private ProductDAO dao = new ProductDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, categoryField, priceField, stockField, minStockField;
    public ProductPanel(){
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        refreshTable();
    }
    private JPanel buildFormPanel(){
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Product"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(12);
        categoryField = new JTextField(10);
        priceField = new JTextField(6);
        stockField = new JTextField(6);
        minStockField = new JTextField(6);
        int col = 0;
        addField(form, gbc, col++, "Item Name:", nameField);
        addField(form, gbc, col++, "Category:", categoryField);
        addField(form, gbc, col++, "Unit Price (Rs):", priceField);
        addField(form, gbc, col++, "Opening Stock:", stockField);
        addField(form, gbc, col++, "Min Reorder Level:", minStockField);
        JButton addBtn = new JButton("Add Product");
        addBtn.addActionListener(this::onAddProduct);
        gbc.gridx = col; gbc.gridy = 1;
        form.add(addBtn, gbc);
        return form;
    }
    private void addField(JPanel form, GridBagConstraints gbc, int col, String label, JTextField field) {
        gbc.gridx = col; gbc.gridy = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridy = 1;
        form.add(field, gbc);
    }
    private JScrollPane buildTablePanel(){
        String[] cols = {"ID", "Name", "Category", "Price (Rs)", "Stock", "Min Level", "Status"};
        model = new DefaultTableModel(cols, 0){
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, sel, focus, row, c);
                String status = (String) t.getValueAt(row, 6);
                if (!sel){
                    if ("LOW STOCK".equals(status)){
                        comp.setBackground(new Color(255, 205, 205));
                    } else {
                        comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        });
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> onDelete());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.add(deleteBtn);
        btnRow.add(refreshBtn);
        wrapper.add(btnRow, BorderLayout.SOUTH);
        JScrollPane outer = new JScrollPane(wrapper);
        outer.setBorder(null);
        return outer;
    }
    private void onAddProduct(ActionEvent e){
        try{
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            int minStock = Integer.parseInt(minStockField.getText().trim());
            if (name.isEmpty()){
                JOptionPane.showMessageDialog(this, "Item name can't be empty.");
                return;
            }
            boolean ok = dao.addProduct(name, category, price, stock, minStock);
            if (ok){
                clearForm();
                refreshTable();
            } else{
                JOptionPane.showMessageDialog(this, "Something went wrong while adding item.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price, Stock and Min Level must be numbers.");
        }
    }
    private void clearForm(){
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        stockField.setText("");
        minStockField.setText("");
    }
    private void onDelete(){
        int row = table.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION){
            dao.deleteProduct(id);
            refreshTable();
        }
    }
    public void refreshTable(){
        model.setRowCount(0);
        List<Product> products = dao.getAllProducts();
        for (Product p : products){
            String status = p.isLowStock() ? "LOW STOCK" : "OK";
            model.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(),
                    String.format("%.2f", p.getPrice()), p.getStock(), p.getMinStock(), status
            });
        }
    }
}
