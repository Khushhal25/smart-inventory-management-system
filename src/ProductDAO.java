import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ProductDAO{
    public boolean addProduct(String name, String category, double price, int stock, int minStock) {
        String sql = "INSERT INTO products(name, category, price, stock, min_stock) VALUES(?,?,?,?,?)";
        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setInt(5, minStock);
            ps.executeUpdate();
            return true;
        } catch (SQLException e){
            System.out.println("addProduct error: " + e.getMessage());
            return false;
        }
    }
    public List<Product> getAllProducts(){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        try (Connection conn = DBHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min_stock")
                ));
            }
        } catch (SQLException e){
            System.out.println("getAllProducts error: " + e.getMessage());
        }
        return list;
    }
    public boolean deleteProduct(int id){
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e){
            System.out.println("deleteProduct error: " + e.getMessage());
            return false;
        }
    }
    public boolean updateStock(int productId, int qtyChange, String type){
        String updateSql = "UPDATE products SET stock = stock + ? WHERE id = ?";
        String logSql = "INSERT INTO stock_log(product_id, type, qty, log_date) VALUES(?,?,?,?)";
        try (Connection conn = DBHelper.connect()){
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updateSql)){
                ps1.setInt(1, qtyChange);
                ps1.setInt(2, productId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(logSql)){
                ps2.setInt(1, productId);
                ps2.setString(2, type);
                ps2.setInt(3, Math.abs(qtyChange));
                ps2.setString(4, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                ps2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e){
            System.out.println("updateStock error: " + e.getMessage());
            return false;
        }
    }
    public Product getProductById(int id){
        String sql = "SELECT * FROM products WHERE id=?";
        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min_stock")
                );
            }
        } catch (SQLException e){
            System.out.println("getProductById error: " + e.getMessage());
        }
        return null;
    }
}
