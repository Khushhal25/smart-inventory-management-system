public class Product{
    private int id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private int minStock;
    public Product(int id, String name, String category, double price, int stock, int minStock){
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
    }
    public int getId(){ return id; }
    public String getName(){ return name; }
    public String getCategory(){ return category; }
    public double getPrice(){ return price; }
    public int getStock(){ return stock; }
    public int getMinStock(){ return minStock; }
    public boolean isLowStock(){
        return stock <= minStock;
    }
    @Override
    public String toString(){
        return name + " (Stock: " + stock + ")";
    }
}
