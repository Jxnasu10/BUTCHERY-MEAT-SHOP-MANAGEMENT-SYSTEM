import java.io.;
import java.time.LocalDate;
import java.util.;

public class MeatShopManager {

private ArrayList<Product> products = new ArrayList<>();
private ArrayList<Customer> customers = new ArrayList<>();
private ArrayList<Sale> sales = new ArrayList<>();
private ArrayList<SaleItem> saleItems = new ArrayList<>();
private ArrayList<String> stockHistory = new ArrayList<>();

private HashMap<String, Product> productMap = new HashMap<>();
private HashMap<String, Customer> customerMap = new HashMap<>();

private final String PRODUCT_FILE = "products.txt";
private final String CUSTOMER_FILE = "customers.txt";
private final String SALES_FILE = "sales.txt";
private final String DETAILS_FILE = "sale_details.txt";
private final String STOCK_FILE = "stock_history.txt";

public MeatShopManager() {
    loadAll();
}

// THE INPUT METHODS

private String input(Scanner sc, String message) {
    System.out.print(message);
    return sc.nextLine().trim();
}

private double number(Scanner sc, String message) {
    while (true) {
        try {
            System.out.print(message);
            double n = Double.parseDouble(sc.nextLine());

            if (Double.isFinite(n)) return n;

        } catch (NumberFormatException e) {
            // Ask again.
        }

        System.out.println("Invalid number. Try again.");
    }
}

private int choice(Scanner sc, String message) {
    while (true) {
        try {
            System.out.print(message);
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
        }
    }
}

private boolean valid(String... values) {
    for (String s : values) {
        if (s == null || s.trim().isEmpty() || s.contains("|"))
            return false;
    }
    return true;
}

// PRODUCT MANAGEMENT

public void addProduct(Scanner sc) {
    String id = input(sc, "Product ID: ");

    if (!id.matches("[A-Za-z0-9_-]+") || productMap.containsKey(id)) {
        System.out.println("Invalid or existing Product ID!");
        return;
    }

    String name = input(sc, "Product Name: ");

    System.out.println("[1] Meat  [2] Fish  [3] Seafood");
    int cat = choice(sc, "Category: ");

    if (!valid(name) || cat < 1 || cat > 3) {
        System.out.println("Invalid product information.");
        return;
    }

    double price = number(sc, "Price per KG: ₱");
    double stock = number(sc, "Initial Stock: ");

    if (price <= 0 || stock < 0) {
        System.out.println("Invalid price or stock.");
        return;
    }

    Product p = cat == 1 ? new Meat(id, name, price, stock)
            : cat == 2 ? new Fish(id, name, price, stock)
            : new Seafood(id, name, price, stock);

    products.add(p);
    productMap.put(id, p);

    stockHistory.add(LocalDate.now() + "|" + id + "|INITIAL STOCK|" + stock);

    saveAll();
    System.out.println("Product added successfully!");
}

public void viewAllProducts() {
    System.out.println("\n================ PRODUCT LIST ================");
    System.out.printf("%-8s %-20s %-12s %-12s %-10s%n",
            "ID", "PRODUCT", "CATEGORY", "PRICE/KG", "STOCK");

    for (Product p : products) p.displayProduct();

    System.out.println("Total Products: " + products.size());
}

public void searchProduct(Scanner sc) {
    Product p = productMap.get(input(sc, "Enter Product ID: "));

    if (p == null) {
        System.out.println("Product not found.");
    } else {
        p.displayProduct();
    }
}

public void updateProduct(Scanner sc) {
    Product p = productMap.get(input(sc, "Enter Product ID: "));

    if (p == null) {
        System.out.println("Product not found.");
        return;
    }

    String name = input(sc, "New Name: ");
    double price = number(sc, "New Price: ₱");

    if (!valid(name) || price <= 0) {
        System.out.println("Invalid information.");
        return;
    }

    p.setProductName(name);
    p.setPrice(price);

    saveProducts();
    System.out.println("Product updated!");
}

public void deleteProduct(Scanner sc) {
    String id = input(sc, "Enter Product ID: ");

    Product p = productMap.get(id);

    if (p == null) {
        System.out.println("Product not found.");
        return;
    }

    for (SaleItem item : saleItems) {
        if (item.getProductId().equals(id)) {
            System.out.println("Cannot delete a product with sales history.");
            return;
        }
    }

    products.remove(p);
    productMap.remove(id);

    saveProducts();
    System.out.println("Product deleted!");
}

// INVENTORY MANAGEMENT

public void viewInventory() {
    System.out.println("\n========== INVENTORY ==========");

    for (Product p : products) {
        System.out.printf("%-20s %.2f KG  %s%n",
                p.getProductName(), p.getStock(),
                p.getStock() <= 5 ? "[LOW STOCK]" : "[AVAILABLE]");
    }
}

public void restockProduct(Scanner sc) {
    Product p = productMap.get(input(sc, "Product ID: "));

    if (p == null) {
        System.out.println("Product not found.");
        return;
    }

    double qty = number(sc, "Quantity to add: ");

    if (qty <= 0) {
        System.out.println("Invalid quantity.");
        return;
    }

    p.setStock(p.getStock() + qty);
    stockHistory.add(LocalDate.now() + "|" + p.getProductId()
            + "|RESTOCK|" + qty);

    saveAll();
    System.out.println("Stock updated!");
}

public void viewStockHistory() {
    System.out.println("\n========== STOCK HISTORY ==========");

    for (String record : stockHistory)
        System.out.println(record);
}

public void lowStockAlert() {
    System.out.println("\n========== LOW STOCK ALERT ==========");

    boolean found = false;

    for (Product p : products) {
        if (p.getStock() <= 5) {
            System.out.println(p.getProductName() + " - "
                    + p.getStock() + " KG");
            found = true;
        }
    }

    if (!found) System.out.println("All products have sufficient stock.");
}

// CUSTOMER MANAGEMENT!!!

public void registerCustomer(Scanner sc) {
    String id = input(sc, "Customer ID: ");

    if (!id.matches("[A-Za-z0-9_-]+") || customerMap.containsKey(id)) {
        System.out.println("Invalid or existing Customer ID!");
        return;
    }

    String name = input(sc, "Customer Name: ");
    String contact = input(sc, "Contact Number: ");

    if (!valid(name, contact)) {
        System.out.println("Invalid information.");
        return;
    }

    Customer c = new Customer(id, name, contact);

    customers.add(c);
    customerMap.put(id, c);

    saveCustomers();
    System.out.println("Customer registered!");
}

public void viewAllCustomers() {
    System.out.println("\n========== CUSTOMER LIST ==========");

    for (Customer c : customers) c.displayCustomer();

    System.out.println("Total Customers: " + customers.size());
}

public void searchCustomer(Scanner sc) {
    Customer c = customerMap.get(input(sc, "Customer ID: "));

    if (c == null) System.out.println("Customer not found.");
    else c.displayCustomer();
}

public void updateCustomer(Scanner sc) {
    Customer c = customerMap.get(input(sc, "Customer ID: "));

    if (c == null) {
        System.out.println("Customer not found.");
        return;
    }

    String name = input(sc, "New Name: ");
    String contact = input(sc, "New Contact: ");

    if (!valid(name, contact)) {
        System.out.println("Invalid information.");
        return;
    }

    c.setName(name);
    c.setContactNumber(contact);

    saveCustomers();
    System.out.println("Customer updated!");
}

public void deleteCustomer(Scanner sc) {
    String id = input(sc, "Customer ID: ");
    Customer c = customerMap.get(id);

    if (c == null) {
        System.out.println("Customer not found.");
        return;
    }

    for (Sale s : sales) {
        if (s.getCustomerId().equals(id)) {
            System.out.println("Cannot delete a customer with sales history.");
            return;
        }
    }

    customers.remove(c);
    customerMap.remove(id);

    saveCustomers();
    System.out.println("Customer deleted!");
}

// PURCHASE SYSTEM!!!

public void newPurchase(Scanner sc) {
    Customer c = customerMap.get(input(sc, "Customer ID: "));

    if (c == null || products.isEmpty()) {
        System.out.println("Customer not found or no products available.");
        return;
    }

    System.out.println("Welcome, " + c.getName() + "!");

    String transactionId = "T" + String.format("%03d", sales.size() + 1);
    ArrayList<SaleItem> cart = new ArrayList<>();

    while (true) {
        viewAllProducts();

        Product p = productMap.get(input(sc, "Product ID: "));

        if (p == null) {
            System.out.println("Product not found.");
            continue;
        }

        double qty = number(sc, "Quantity (KG): ");
        double inCart = 0;

        for (SaleItem item : cart) {
            if (item.getProductId().equals(p.getProductId()))
                inCart += item.getQuantity();
        }

        if (qty <= 0 || qty + inCart > p.getStock()) {
            System.out.println("Invalid quantity or insufficient stock.");
            continue;
        }

        cart.add(new SaleItem(transactionId, p.getProductId(),
                p.getProductName(), qty, p.getPrice()));

        if (!input(sc, "Add another product? (Y/N): ").equalsIgnoreCase("Y"))
            break;
    }

    if (cart.isEmpty()) {
        System.out.println("Empty cart.");
        return;
    }

    double total = 0;
    for (SaleItem item : cart) total += item.getSubtotal();

    System.out.printf("Total: ₱%.2f%n", total);

    double cash;
    do {
        cash = number(sc, "Cash received: ₱");
        if (cash < total) System.out.println("Insufficient payment!");
    } while (cash < total);

    Sale sale = new Sale(transactionId, c.getCustomerId(),
            c.getName(), LocalDate.now().toString(), total, cash);

    for (SaleItem item : cart) {
        Product p = productMap.get(item.getProductId());
        p.setStock(p.getStock() - item.getQuantity());

        stockHistory.add(LocalDate.now() + "|" + p.getProductId()
                + "|SALE|" + item.getQuantity());
    }

    sales.add(sale);
    saleItems.addAll(cart);

    saveAll();
    printReceipt(sale, cart);
}

private void printReceipt(Sale sale, ArrayList<SaleItem> cart) {
    System.out.println("\n========================================");
    System.out.println("             FRESH CUT");
    System.out.println("          MEAT SHOP RECEIPT");
    System.out.println("========================================");
    System.out.println("Transaction: " + sale.getTransactionId());
    System.out.println("Customer: " + sale.getCustomerName());
    System.out.println("Date: " + sale.getDate());
    System.out.println("----------------------------------------");

    for (SaleItem item : cart) {
        System.out.printf("%-18s %.2f KG  ₱%.2f%n",
                item.getProductName(), item.getQuantity(),
                item.getSubtotal());
    }

    System.out.println("----------------------------------------");
    System.out.printf("TOTAL:          ₱%.2f%n", sale.getTotal());
    System.out.printf("CASH:           ₱%.2f%n", sale.getCashReceived());
    System.out.printf("CHANGE:         ₱%.2f%n", sale.getChange());
    System.out.println("========================================");
    System.out.println("     THANK YOU FOR YOUR PURCHASE!");
    System.out.println("========================================");
}

// SALES RECORDS AND REPORTS

public void viewSalesRecords() {
    System.out.println("\n========== SALES RECORDS ==========");

    for (Sale s : sales) s.displaySale();

    System.out.println("Total Transactions: " + sales.size());
}

public void salesReport() {
    String today = LocalDate.now().toString();

    double total = 0, cash = 0, change = 0, qty = 0;
    int transactions = 0;

    HashMap<String, Double> sold = new HashMap<>();

    for (Sale s : sales) {
        if (s.getDate().equals(today)) {
            total += s.getTotal();
            cash += s.getCashReceived();
            change += s.getChange();
            transactions++;

            for (SaleItem item : saleItems) {
                if (item.getTransactionId().equals(s.getTransactionId())) {
                    qty += item.getQuantity();
                    sold.put(item.getProductId(),
                            sold.getOrDefault(item.getProductId(), 0.0)
                            + item.getQuantity());
                }
            }
        }
    }

    System.out.println("\n========== DAILY SALES REPORT ==========");
    System.out.println("Date: " + today);
    System.out.println("Transactions: " + transactions);
    System.out.printf("Products Sold: %.2f KG%n", qty);
    System.out.printf("Total Sales: ₱%.2f%n", total);
    System.out.printf("Cash Received: ₱%.2f%n", cash);
    System.out.printf("Change Given: ₱%.2f%n", change);

    System.out.println("\n========== TOP SELLING PRODUCTS ==========");

    sold.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(5)
            .forEach(entry -> {
                Product p = productMap.get(entry.getKey());
                String name = p == null ? entry.getKey() : p.getProductName();
                System.out.printf("%-20s %.2f KG%n", name, entry.getValue());
            });
}

// FILE HANDLING!!!

private void saveFile(String file, ArrayList<String> records) {
    try (PrintWriter w = new PrintWriter(new FileWriter(file))) {
        for (String record : records) w.println(record);
    } catch (IOException e) {
        System.out.println("Error saving " + file);
    }
}

private ArrayList<String> readFile(String file) {
    ArrayList<String> records = new ArrayList<>();

    try (BufferedReader r = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = r.readLine()) != null) records.add(line);
    } catch (IOException e) {
        // File may not exist yet.
    }

    return records;
}

private void saveProducts() {
    ArrayList<String> records = new ArrayList<>();

    for (Product p : products) {
        records.add(p.getProductId() + "|" + p.getProductName() + "|"
                + p.getCategory() + "|" + p.getPrice() + "|" + p.getStock());
    }

    saveFile(PRODUCT_FILE, records);
}

private void saveCustomers() {
    ArrayList<String> records = new ArrayList<>();

    for (Customer c : customers) {
        records.add(c.getCustomerId() + "|" + c.getName()
                + "|" + c.getContactNumber());
    }

    saveFile(CUSTOMER_FILE, records);
}

private void saveSales() {
    ArrayList<String> records = new ArrayList<>();

    for (Sale s : sales) {
        records.add(s.getTransactionId() + "|" + s.getCustomerId()
                + "|" + s.getCustomerName() + "|" + s.getDate()
                + "|" + s.getTotal() + "|" + s.getCashReceived()
                + "|" + s.getChange());
    }

    saveFile(SALES_FILE, records);
}

private void saveSaleItems() {
    ArrayList<String> records = new ArrayList<>();

    for (SaleItem i : saleItems) {
        records.add(i.getTransactionId() + "|" + i.getProductId()
                + "|" + i.getProductName() + "|" + i.getQuantity()
                + "|" + i.getPrice() + "|" + i.getSubtotal());
    }

    saveFile(DETAILS_FILE, records);
}

private void saveStockHistory() {
    saveFile(STOCK_FILE, stockHistory);
}

private void loadAll() {
    for (String line : readFile(PRODUCT_FILE)) {
        try {
            String[] d = line.split("\\|", -1);
            if (d.length != 5 || productMap.containsKey(d[0])) continue;

            double price = Double.parseDouble(d[3]);
            double stock = Double.parseDouble(d[4]);

            Product p = d[2].equalsIgnoreCase("Meat")
                    ? new Meat(d[0], d[1], price, stock)
                    : d[2].equalsIgnoreCase("Fish")
                    ? new Fish(d[0], d[1], price, stock)
                    : d[2].equalsIgnoreCase("Seafood")
                    ? new Seafood(d[0], d[1], price, stock) : null;

            if (p != null) {
                products.add(p);
                productMap.put(d[0], p);
            }
        } catch (Exception e) {
            System.out.println("Invalid product record skipped.");
        }
    }

    for (String line : readFile(CUSTOMER_FILE)) {
        String[] d = line.split("\\|", -1);
        if (d.length == 3 && !customerMap.containsKey(d[0])) {
            Customer c = new Customer(d[0], d[1], d[2]);
            customers.add(c);
            customerMap.put(d[0], c);
        }
    }

    for (String line : readFile(SALES_FILE)) {
        try {
            String[] d = line.split("\\|", -1);
            if (d.length == 7) {
                sales.add(new Sale(d[0], d[1], d[2], d[3],
                        Double.parseDouble(d[4]),
                        Double.parseDouble(d[5])));
            }
        } catch (Exception e) {
            System.out.println("Invalid sale record skipped.");
        }
    }

    for (String line : readFile(DETAILS_FILE)) {
        try {
            String[] d = line.split("\\|", -1);
            if (d.length == 6) {
                saleItems.add(new SaleItem(d[0], d[1], d[2],
                        Double.parseDouble(d[3]),
                        Double.parseDouble(d[4])));
            }
        } catch (Exception e) {
            System.out.println("Invalid sale detail skipped.");
        }
    }

    stockHistory.addAll(readFile(STOCK_FILE));
}

public void saveAll() {
    saveProducts();
    saveCustomers();
    saveSales();
    saveSaleItems();
    saveStockHistory();
}
}
