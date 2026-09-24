import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class SalesDAO{
    public double recordSale(int productId, String productName, int qty, double unitPrice, double discountPct, double taxPct) {
        double subtotal = qty * unitPrice;
        double discountAmt = subtotal * (discountPct / 100.0);
        double afterDiscount = subtotal - discountAmt;
        double taxAmt = afterDiscount * (taxPct / 100.0);
        double total = afterDiscount + taxAmt;
        String insertSale = "INSERT INTO sales(product_id, product_name, qty, unit_price, discount, tax, total, sale_date) VALUES(?,?,?,?,?,?,?,?)";
        String reduceStock = "UPDATE products SET stock = stock - ? WHERE id = ?";
        try (Connection conn = DBHelper.connect()){
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insertSale)){
                ps.setInt(1, productId);
                ps.setString(2, productName);
                ps.setInt(3, qty);
                ps.setDouble(4, unitPrice);
                ps.setDouble(5, discountPct);
                ps.setDouble(6, taxPct);
                ps.setDouble(7, total);
                ps.setString(8, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(reduceStock)){
                ps2.setInt(1, qty);
                ps2.setInt(2, productId);
                ps2.executeUpdate();
            }
            conn.commit();
            return total;
        } catch (SQLException e){
            System.out.println("recordSale error: " + e.getMessage());
            return -1;
        }
    }
    public double getTotalSalesValue(){
        String sql = "SELECT SUM(total) as t FROM sales";
        try (Connection conn = DBHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            if (rs.next()) return rs.getDouble("t");
        } catch (SQLException e){
            System.out.println("getTotalSalesValue error: " + e.getMessage());
        }
        return 0;
    }
    public List<Object[]> getBestSellers(int limit){
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT product_name, SUM(qty) as totalQty FROM sales GROUP BY product_name ORDER BY totalQty DESC LIMIT ?";
        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                list.add(new Object[]{rs.getString("product_name"), rs.getInt("totalQty")});
            }
        } catch (SQLException e){
            System.out.println("getBestSellers error: " + e.getMessage());
        }
        return list;
    }
    public List<Object[]> getRecentSales(int limit){
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT product_name, qty, total, sale_date FROM sales ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                list.add(new Object[]{rs.getString("product_name"), rs.getInt("qty"), rs.getDouble("total"), rs.getString("sale_date")});
            }
        }catch (SQLException e){
            System.out.println("getRecentSales error: " + e.getMessage());
        }
        return list;
    }
}
