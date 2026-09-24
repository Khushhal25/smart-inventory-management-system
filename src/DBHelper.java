import java.sql.*;
public class DBHelper{
    static final String DB_URL = "jdbc:sqlite:inventory.db";
    public static Connection connect(){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e){
            System.out.println("DB connection failed: " + e.getMessage());
        }
        return conn;
    }
    public static void setup(){
        String products = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "category TEXT," +
                "price REAL NOT NULL," +
                "stock INTEGER NOT NULL DEFAULT 0," +
                "min_stock INTEGER NOT NULL DEFAULT 5" +
                ");";
        String stockLog = "CREATE TABLE IF NOT EXISTS stock_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER," +
                "type TEXT," + // "IN" or "OUT"
                "qty INTEGER," +
                "log_date TEXT," +
                "FOREIGN KEY(product_id) REFERENCES products(id)" +
                ");";
        String sales = "CREATE TABLE IF NOT EXISTS sales (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER," +
                "product_name TEXT," +
                "qty INTEGER," +
                "unit_price REAL," +
                "discount REAL," +
                "tax REAL," +
                "total REAL," +
                "sale_date TEXT," +
                "FOREIGN KEY(product_id) REFERENCES products(id)" +
                ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()){
            stmt.execute(products);
            stmt.execute(stockLog);
            stmt.execute(sales);
        } catch (SQLException e){
            System.out.println("Table creation error: " + e.getMessage());
        }
    }
}
