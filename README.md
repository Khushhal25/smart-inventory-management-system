# Smart Inventory & Order Management System

Desktop application (Java Swing + SQLite) for tracking stock, logging
purchases/sales, warning on low stock, and generating simple receipts.

## Requirements
- Java JDK 8 or higher installed (`java -version` to check)
- That's it — SQLite driver is already included as `sqlite-jdbc.jar`, no server/DB install needed.

## How to Run

### Windows
Double click `run.bat` (or run it from cmd inside this folder).

### Mac/Linux
```
chmod +x run.sh
./run.sh
```

### Manual (any OS)
```
javac -cp sqlite-jdbc.jar -d bin src/*.java
java -cp "sqlite-jdbc.jar:bin" Main      (use ; instead of : on Windows)
```

A file called `inventory.db` will be created automatically in this folder
on first run — that's your database, no setup needed.

## Project Structure
```
src/
  Main.java          -> entry point
  MainWindow.java     -> main window with 4 tabs
  DBHelper.java       -> SQLite connection + table creation
  Product.java        -> product model
  ProductDAO.java      -> product CRUD + stock updates
  SalesDAO.java        -> checkout/sales + reports queries
  ProductPanel.java    -> "Products" tab (catalog, add/delete)
  StockPanel.java      -> "Stock Entry" tab (restock/remove)
  CheckoutPanel.java   -> "Checkout" tab (billing, receipt)
  ReportsPanel.java    -> "Reports" tab (revenue, best sellers)
```

## Features covered
- Product catalog: name, category, price, stock, min reorder level
- Stock in/out entry with logging
- Low-stock visual warnings (red rows in table + alert popup)
- Checkout with discount %, tax %, auto stock deduction, text receipt saved to file
- Reports: total sales revenue, current inventory value, best sellers, recent sales list

## Suggested demo flow (for viva/submission)
1. Products tab -> add 3-4 items (keep min stock levels realistic, e.g. 10)
2. Stock Entry tab -> restock one item, see stock update
3. Checkout tab -> sell an item until it goes below its min level -> see the low stock popup
4. Reports tab -> show total revenue and best seller updating live
