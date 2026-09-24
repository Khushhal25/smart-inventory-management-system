import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class CheckoutPanel extends JPanel{
    private ProductDAO productDao = new ProductDAO();
    private SalesDAO salesDao = new SalesDAO();
    private JComboBox<Product> productBox;
    private JTextField qtyField, discountField, taxField;
    private JTextArea billArea;
    public CheckoutPanel(){
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("New Sale / Checkout"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        productBox = new JComboBox<>();
        loadProducts();
        qtyField = new JTextField(5);
        discountField = new JTextField("0", 5);
        taxField = new JTextField("0", 5);
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Item:"), gbc);
        gbc.gridx = 1; form.add(productBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; form.add(qtyField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Discount %:"), gbc);
        gbc.gridx = 1; form.add(discountField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Tax %:"), gbc);
        gbc.gridx = 1; form.add(taxField, gbc);
        JButton checkoutBtn = new JButton("Complete Sale & Generate Receipt");
        checkoutBtn.addActionListener(e -> completeSale());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(checkoutBtn, gbc);
        add(form, BorderLayout.NORTH);
        billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billArea.setText("Receipt will show here after checkout...");
        JScrollPane scroll = new JScrollPane(billArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Last Receipt"));
        add(scroll, BorderLayout.CENTER);
    }
    private void loadProducts(){
        productBox.removeAllItems();
        List<Product> products = productDao.getAllProducts();
        for (Product p : products){
            if (p.getStock() > 0) productBox.addItem(p);
        }
    }
    public void refresh(){
        loadProducts();
    }
    private void completeSale(){
        Product p = (Product) productBox.getSelectedItem();
        if (p == null){
            JOptionPane.showMessageDialog(this, "No item selected / everything is out of stock.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            double discount = Double.parseDouble(discountField.getText().trim());
            double tax = Double.parseDouble(taxField.getText().trim());
            if (qty <= 0){
                JOptionPane.showMessageDialog(this, "Quantity should be more than 0.");
                return;
            }
            if (qty > p.getStock()){
                JOptionPane.showMessageDialog(this, "Only " + p.getStock() + " in stock.");
                return;
            }
            double total = salesDao.recordSale(p.getId(), p.getName(), qty, p.getPrice(), discount, tax);
            if (total < 0) {
                JOptionPane.showMessageDialog(this, "Sale failed, try again.");
                return;
            }
            String receipt = buildReceipt(p, qty, discount, tax, total);
            billArea.setText(receipt);
            saveReceiptToFile(receipt, p.getName());
            qtyField.setText("");
            loadProducts();
            Product updated = productDao.getProductById(p.getId());
            if (updated.isLowStock()){
                JOptionPane.showMessageDialog(this, "Note: " + updated.getName() + " stock is now low (" + updated.getStock() + " left).");
            }
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Qty, Discount and Tax must be valid numbers.");
        }
    }
    private String buildReceipt(Product p, int qty, double discount, double tax, double total) {
        double subtotal = qty * p.getPrice();
        double discAmt = subtotal * discount / 100.0;
        double taxAmt = (subtotal - discAmt) * tax / 100.0;
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        StringBuilder sb = new StringBuilder();
        sb.append("            SALES RECEIPT\n");
        sb.append("=========================================\n");
        sb.append("Date: ").append(time).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-20s x%-5d Rs.%.2f\n", p.getName(), qty, p.getPrice()));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Subtotal:          Rs. %.2f\n", subtotal));
        sb.append(String.format("Discount (%.1f%%):   Rs. %.2f\n", discount, discAmt));
        sb.append(String.format("Tax (%.1f%%):        Rs. %.2f\n", tax, taxAmt));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("TOTAL:             Rs. %.2f\n", total));
        sb.append("=========================================\n");
        sb.append("      Thank you for shopping with us!\n");
        return sb.toString();
    }
    private void saveReceiptToFile(String receipt, String productName){
        try{
            String filename = "receipt_" + System.currentTimeMillis() + ".txt";
            FileWriter fw = new FileWriter(filename);
            fw.write(receipt);
            fw.close();
        } catch (IOException e){
            System.out.println("Couldn't save receipt file: " + e.getMessage());
        }
    }
}
