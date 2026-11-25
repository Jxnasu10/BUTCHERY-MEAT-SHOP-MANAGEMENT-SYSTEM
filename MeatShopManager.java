
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MeatShopManager {

private ArrayList<Product> products;
private ArrayList<Customer> customers;
private ArrayList<Sale> sales;
private ArrayList<SaleItem> saleItems;
private ArrayList<String> stockHistory;

private HashMap<String, Product> productMap;
private HashMap<String, Customer> customerMap;

private final String PRODUCT_FILE = "products.txt";
private final String CUSTOMER_FILE = "customers.txt";
private final String SALES_FILE = "sales.txt";
private final String DETAILS_FILE = "sale_details.txt";
private final String STOCK_FILE = "stock_history.txt";

public MeatShopManager() {

    products = new ArrayList<Product>();
    customers = new ArrayList<Customer>();
    sales = new ArrayList<Sale>();
    saleItems = new ArrayList<SaleItem>();
    stockHistory = new ArrayList<String>();

    productMap = new HashMap<String, Product>();
    customerMap = new HashMap<String, Customer>();

    loadProducts();
    loadCustomers();
    loadSales();
    loadSaleItems();
    loadStockHistory();
}

// INPUT VALIDATION

private String readText(Scanner sc, String message) {
    System.out.print(message);
    return sc.nextLine().trim();
}

private double readDouble(Scanner sc, String message) {

    while (true) {
        try {
            System.out.print(message);

            double value = Double.parseDouble(sc.nextLine());

            if (Double.isNaN(value) || Double.isInfinite(value)) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            return value;

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}

private int readInt(Scanner sc, String message) {

    while (true) {
        try {
            System.out.print(message);
            return Integer.parseInt(sc.nextLine());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a whole number.");
        }
    }
}

private boolean validText(String... values) {

    for (String value : values) {
        if (value == null || value.trim().isEmpty()
                || value.contains("|")) {
            return false;
        }
    }

    return true;
}

private boolean validProductId(String id) {
    return id.matches("[A-Za-z0-9_-]+");
}

// PRODUCT MANAGEMENT

public void addProduct(Scanner sc) {

    System.out.println("\n========== ADD NEW PRODUCT ==========");

    String id = readText(sc, "Enter Product ID: ");

    if (!validProductId(id)) {
        System.out.println("Use only letters, numbers, _ or - in the ID.");
        return;
    }

    if (productMap.containsKey(id)) {
        System.out.println("Product ID already exists!");
        return;
    }

    String name = readText(sc, "Enter Product Name: ");

    System.out.println("\nSelect Category:");
    System.out.println("[1] Meat");
    System.out.println("[2] Fish");
    System.out.println("[3] Seafood");

    int category = readInt(sc, "Enter choice: ");

    if (category < 1 || category > 3) {
        System.out.println("Invalid category!");
        return;
    }

    double price = readDouble(sc, "Enter Price per KG: ₱");
    double stock = readDouble(sc, "Enter Initial Stock (KG): ");

    if (!validText(name) || price <= 0 || stock < 0) {
        System.out.println("Invalid product information!");
        return;
    }

    Product product;

    if (category == 1) {
        product = new Meat(id, name, price, stock);
    } else if (category == 2) {
        product = new Fish(id, name, price, stock);
    } else {
        product = new Seafood(id, name, price, stock);
    }

    products.add(product);
    productMap.put(id, product);

    stockHistory.add(
            LocalDate.now() + "|" + id + "|INITIAL STOCK|" + stock
    );

    saveProducts();
    saveStockHistory();

    System.out.println("Product added successfully!");
}

public void viewAllProducts() {

    System.out.println("\n================ PRODUCT INVENTORY ================");

    if (products.isEmpty()) {
        System.out.println("No products available.");
        return;
    }

    System.out.printf("%-8s %-20s %-12s %-12s %-10s%n",
            "ID", "PRODUCT NAME", "CATEGORY", "PRICE/KG", "STOCK");

    System.out.println("--------------------------------------------------------------");

    for (Product product : products) {
        product.displayProduct();
    }

    System.out.println("--------------------------------------------------------------");
    System.out.println("Total Products: " + products.size());
}

public void searchProduct(Scanner sc) {

    String id = readText(sc, "Enter Product ID: ");

    Product product = productMap.get(id);

    if (product == null) {
        System.out.println("Product not found.");
        return;
    }

    System.out.println("\nProduct Information:");
    System.out.printf("%-8s %-20s %-12s %-12s %-10s%n",
            "ID", "PRODUCT NAME", "CATEGORY", "PRICE/KG", "STOCK");

    product.displayProduct();
}

public void updateProduct(Scanner sc) {

    String id = readText(sc, "Enter Product ID to update: ");

    Product product = productMap.get(id);

    if (product == null) {
        System.out.println("Product not found.");
        return;
    }

    String name = readText(sc, "Enter New Product Name: ");
    double price = readDouble(sc, "Enter New Price per KG: ₱");

    if (!validText(name) || price <= 0) {
        System.out.println("Invalid product information!");
        return;
    }

    product.setProductName(name);
    product.setPrice(price);

    saveProducts();

    System.out.println("Product updated successfully!");
}

public void deleteProduct(Scanner sc) {

    String id = readText(sc, "Enter Product ID to delete: ");

    Product product = productMap.get(id);

    if (product == null) {
        System.out.println("Product not found.");
        return;
    }

    for (SaleItem item : saleItems) {
        if (item.getProductId().equals(id)) {
            System.out.println(
                    "Cannot delete this product because it has sales history."
            );
            return;
        }
    }

    products.remove(product);
    productMap.remove(id);

    saveProducts();

    System.out.println("Product deleted successfully!");
}

// INVENTORY MANAGEMENT

public void viewInventory() {

    System.out.println("\n============== CURRENT INVENTORY ==============");

    System.out.printf("%-8s %-20s %-15s %-12s%n",
            "ID", "PRODUCT", "STOCK", "STATUS");

    System.out.println("------------------------------------------------");

    for (Product product : products) {

        String status = product.getStock() <= 5
                ? "LOW STOCK!"
                : "AVAILABLE";

        System.out.printf("%-8s %-20s %-15.2f %-12s%n",
                product.getProductId(),
                product.getProductName(),
                product.getStock(),
                status);
    }
}

public void restockProduct(Scanner sc) {

    String id = readText(sc, "Enter Product ID to restock: ");

    Product product = productMap.get(id);

    if (product == null) {
        System.out.println("Product not found.");
        return;
    }

    double quantity = readDouble(sc, "Enter Quantity to Add (KG): ");

    if (quantity <= 0) {
        System.out.println("Invalid quantity!");
        return;
    }

    product.setStock(product.getStock() + quantity);

    stockHistory.add(
            LocalDate.now() + "|" + id + "|RESTOCK|" + quantity
    );

    saveProducts();
    saveStockHistory();

    System.out.println("Stock updated successfully!");
    System.out.println("New Stock: " + product.getStock() + " KG");
}

public void viewStockHistory() {

    System.out.println("\n================ STOCK HISTORY ================");

    if (stockHistory.isEmpty()) {
        System.out.println("No stock history found.");
        return;
    }

    System.out.printf("%-15s %-12s %-15s %-12s%n",
            "DATE", "PRODUCT ID", "ACTION", "QUANTITY");

    System.out.println("------------------------------------------------");

    for (String record : stockHistory) {
        String[] data = record.split("\\|", -1);

        if (data.length == 4) {
            System.out.printf("%-15s %-12s %-15s %-12s%n",
                    data[0], data[1], data[2], data[3] + " KG");
        }
    }
}

public void lowStockAlert() {

    System.out.println("\n============== LOW STOCK PRODUCTS ==============");

    boolean found = false;

    for (Product product : products) {

        if (product.getStock() <= 5) {

            System.out.printf("%-20s %-10.2f KG%n",
                    product.getProductName(),
                    product.getStock());

            found = true;
        }
    }

    if (!found) {
        System.out.println("All products have sufficient stock!");
    }
}

// CUSTOMER MANAGEMENT

public void registerCustomer(Scanner sc) {

    System.out.println("\n========== REGISTER CUSTOMER ==========");

    String id = readText(sc, "Enter Customer ID: ");

    if (!validProductId(id)) {
        System.out.println("Use only letters, numbers, _ or - in the ID.");
        return;
    }

    if (customerMap.containsKey(id)) {
        System.out.println("Customer ID already exists!");
        return;
    }

    String name = readText(sc, "Enter Customer Name: ");
    String contact = readText(sc, "Enter Contact Number: ");

    if (!validText(name, contact)) {
        System.out.println("Invalid customer information!");
        return;
    }

    Customer customer = new Customer(id, name, contact);

    customers.add(customer);
    customerMap.put(id, customer);

    saveCustomers();

    System.out.println("Customer registered successfully!");
}

public void viewAllCustomers() {

    System.out.println("\n================ CUSTOMER LIST ================");

    if (customers.isEmpty()) {
        System.out.println("No customers registered.");
        return;
    }

    System.out.printf("%-10s %-25s %-15s%n",
            "ID", "CUSTOMER NAME", "CONTACT");

    System.out.println("------------------------------------------------");

    for (Customer customer : customers) {
        customer.displayCustomer();
    }
}

public void searchCustomer(Scanner sc) {

    String id = readText(sc, "Enter Customer ID: ");

    Customer customer = customerMap.get(id);

    if (customer == null) {
        System.out.println("Customer not found.");
        return;
    }

    customer.displayCustomer();
}

public void updateCustomer(Scanner sc) {

    String id = readText(sc, "Enter Customer ID to update: ");

    Customer customer = customerMap.get(id);

    if (customer == null) {
        System.out.println("Customer not found.");
        return;
    }

    String name = readText(sc, "Enter New Name: ");
    String contact = readText(sc, "Enter New Contact Number: ");

    if (!validText(name, contact)) {
        System.out.println("Invalid customer information!");
        return;
    }

    customer.setName(name);
    customer.setContactNumber(contact);

    saveCustomers();

    System.out.println("Customer updated successfully!");
}

public void deleteCustomer(Scanner sc) {

    String id = readText(sc, "Enter Customer ID to delete: ");

    Customer customer = customerMap.get(id);

    if (customer == null) {
        System.out.println("Customer not found.");
        return;
    }

    for (Sale sale : sales) {
        if (sale.getCustomerId().equals(id)) {
            System.out.println(
                    "Cannot delete customer because they have sales history."
            );
            return;
        }
    }

    customers.remove(customer);
    customerMap.remove(id);

    saveCustomers();

    System.out.println("Customer deleted successfully!");
}

// CUSTOMER PURCHASE

public void newPurchase(Scanner sc) {

    if (products.isEmpty()) {
        System.out.println("No products available.");
        return;
    }

    String customerId = readText(sc, "Enter Customer ID: ");

    Customer customer = customerMap.get(customerId);

    if (customer == null) {
        System.out.println("Customer not found. Register the customer first.");
        return;
    }

    System.out.println("\nWelcome, " + customer.getName() + "!");

    ArrayList<SaleItem> cart = new ArrayList<SaleItem>();

    String transactionId = generateTransactionId();

    while (true) {

        viewAllProducts();

        String productId = readText(sc, "\nEnter Product ID: ");

        Product product = productMap.get(productId);

        if (product == null) {
            System.out.println("Product not found!");
            continue;
        }

        double quantity = readDouble(sc, "Enter Quantity (KG): ");

        if (quantity <= 0) {
            System.out.println("Invalid quantity!");
            continue;
        }

        double alreadyInCart = 0;

        for (SaleItem item : cart) {
            if (item.getProductId().equals(productId)) {
                alreadyInCart += item.getQuantity();
            }
        }

        if (quantity + alreadyInCart > product.getStock()) {
            System.out.println("Not enough stock available!");
            continue;
        }

        SaleItem item = new SaleItem(
                transactionId,
                product.getProductId(),
                product.getProductName(),
                quantity,
                product.getPrice()
        );

        cart.add(item);

        System.out.println(product.getProductName() + " added to cart!");

        String answer = readText(sc, "Add another product? (Y/N): ");

        if (!answer.equalsIgnoreCase("Y")) {
            break;
        }
    }

    if (cart.isEmpty()) {
        System.out.println("Purchase cancelled. Cart is empty.");
        return;
    }

    double total = 0;

    for (SaleItem item : cart) {
        total += item.getSubtotal();
    }

    System.out.printf("%nTotal Amount: ₱%.2f%n", total);

    double cash;

    while (true) {

        cash = readDouble(sc, "Enter Cash Received: ₱");

        if (cash >= total) {
            break;
        }

        System.out.println("Insufficient payment! Please try again.");
    }

    Sale sale = new Sale(
            transactionId,
            customer.getCustomerId(),
            customer.getName(),
            LocalDate.now().toString(),
            total,
            cash
    );

    // Deduct purchased products from inventory.

    for (SaleItem item : cart) {

        Product product = productMap.get(item.getProductId());

        product.setStock(product.getStock() - item.getQuantity());

        stockHistory.add(
                LocalDate.now() + "|"
                + product.getProductId()
                + "|SALE|"
                + item.getQuantity()
        );
    }

    sales.add(sale);
    saleItems.addAll(cart);

    saveAll();

    printReceipt(sale, cart);
}

private String generateTransactionId() {

    int highest = 0;

    for (Sale sale : sales) {

        String id = sale.getTransactionId();

        try {
            int number = Integer.parseInt(id.substring(1));

            if (number > highest) {
                highest = number;
            }

        } catch (Exception e) {
            // Ignore transaction IDs that do not follow T<number>.
        }
    }

    return String.format("T%03d", highest + 1);
}

private void printReceipt(Sale sale, ArrayList<SaleItem> cart) {

    System.out.println("\n================================================");
    System.out.println("                 FRESH CUT");
    System.out.println("              MEAT SHOP RECEIPT");
    System.out.println("================================================");

    System.out.println("Transaction ID: " + sale.getTransactionId());
    System.out.println("Date: " + sale.getDate());
    System.out.println("Customer: " + sale.getCustomerName());

    System.out.println("------------------------------------------------");

    System.out.printf("%-18s %-8s %-10s%n",
            "PRODUCT", "QTY", "SUBTOTAL");

    for (SaleItem item : cart) {

        System.out.printf("%-18s %-8.2f ₱%-10.2f%n",
                item.getProductName(),
                item.getQuantity(),
                item.getSubtotal());
    }

    System.out.println("------------------------------------------------");

    System.out.printf("TOTAL AMOUNT:       ₱%.2f%n", sale.getTotal());
    System.out.printf("CASH RECEIVED:      ₱%.2f%n", sale.getCashReceived());
    System.out.printf("CHANGE:             ₱%.2f%n", sale.getChange());

    System.out.println("================================================");
    System.out.println("       THANK YOU FOR YOUR PURCHASE!");
    System.out.println("================================================");
}

// SALES RECORDS

public void viewSalesRecords() {

    System.out.println("\n================ SALES RECORDS ================");

    if (sales.isEmpty()) {
        System.out.println("No sales records found.");
        return;
    }

    System.out.printf("%-10s %-22s %-12s %-12s%n",
            "TRANS ID", "CUSTOMER", "DATE", "TOTAL");

    System.out.println("------------------------------------------------");

    for (Sale sale : sales) {
        sale.displaySale();
    }

    System.out.println("------------------------------------------------");
    System.out.println("Total Transactions: " + sales.size());
}

// SALES REPORT

public void salesReport() {

    System.out.println("\n============== SALES REPORT ==============");

    String today = LocalDate.now().toString();

    double totalSales = 0;
    double totalCash = 0;
    double totalChange = 0;

    int transactions = 0;
    double totalQuantity = 0;

    HashMap<String, Double> productSales =
            new HashMap<String, Double>();

    for (Sale sale : sales) {

        if (sale.getDate().equals(today)) {

            totalSales += sale.getTotal();
            totalCash += sale.getCashReceived();
            totalChange += sale.getChange();

            transactions++;

            for (SaleItem item : saleItems) {

                if (item.getTransactionId().equals(
                        sale.getTransactionId())) {

                    totalQuantity += item.getQuantity();

                    double oldQuantity =
                            productSales.containsKey(item.getProductId())
                            ? productSales.get(item.getProductId())
                            : 0.0;

                    productSales.put(
                            item.getProductId(),
                            oldQuantity + item.getQuantity()
                    );
                }
            }
        }
    }

    System.out.println("Date: " + today);
    System.out.println("------------------------------------------");

    System.out.println("Total Transactions: " + transactions);
    System.out.printf("Total Quantity Sold: %.2f KG%n", totalQuantity);

    System.out.printf("Total Sales: ₱%.2f%n", totalSales);
    System.out.printf("Cash Received: ₱%.2f%n", totalCash);
    System.out.printf("Change Given: ₱%.2f%n", totalChange);

    System.out.println("------------------------------------------");
    System.out.println("          TOP SELLING PRODUCTS");
    System.out.println("------------------------------------------");

    if (productSales.isEmpty()) {
        System.out.println("No sales today.");
        return;
    }

    ArrayList<String> sortedProducts =
            new ArrayList<String>(productSales.keySet());

    sortedProducts.sort((a, b) ->
            Double.compare(productSales.get(b), productSales.get(a))
    );

    int rank = 1;

    for (String id : sortedProducts) {

        Product product = productMap.get(id);

        String name = product != null
                ? product.getProductName()
                : id;

        System.out.printf("%d. %-20s %.2f KG%n",
                rank,
                name,
                productSales.get(id));

        rank++;

        if (rank > 5) {
            break;
        }
    }

    System.out.println("==========================================");
}

// FILE HANDLING

private void saveProducts() {

    try (PrintWriter writer =
                 new PrintWriter(new FileWriter(PRODUCT_FILE))) {

        for (Product product : products) {

            writer.println(
                    product.getProductId() + "|"
                    + product.getProductName() + "|"
                    + product.getCategory() + "|"
                    + product.getPrice() + "|"
                    + product.getStock()
            );
        }

    } catch (IOException e) {
        System.out.println("Error saving products: " + e.getMessage());
    }
}

private void loadProducts() {

    File file = new File(PRODUCT_FILE);

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

        String line;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split("\\|", -1);

            if (data.length != 5) {
                continue;
            }

            String id = data[0];
            String name = data[1];
            String category = data[2];

            double price = Double.parseDouble(data[3]);
            double stock = Double.parseDouble(data[4]);

            Product product = null;

            if (category.equalsIgnoreCase("Meat")) {
                product = new Meat(id, name, price, stock);

            } else if (category.equalsIgnoreCase("Fish")) {
                product = new Fish(id, name, price, stock);

            } else if (category.equalsIgnoreCase("Seafood")) {
                product = new Seafood(id, name, price, stock);
            }

            if (product != null && !productMap.containsKey(id)) {
                products.add(product);
                productMap.put(id, product);
            }
        }

    } catch (Exception e) {
        System.out.println("Error loading products: " + e.getMessage());
    }
}

private void saveCustomers() {

    try (PrintWriter writer =
                 new PrintWriter(new FileWriter(CUSTOMER_FILE))) {

        for (Customer customer : customers) {

            writer.println(
                    customer.getCustomerId() + "|"
                    + customer.getName() + "|"
                    + customer.getContactNumber()
            );
        }

    } catch (IOException e) {
        System.out.println("Error saving customers: " + e.getMessage());
    }
}

private void loadCustomers() {

    File file = new File(CUSTOMER_FILE);

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

        String line;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split("\\|", -1);

            if (data.length == 3 && !customerMap.containsKey(data[0])) {

                Customer customer =
                        new Customer(data[0], data[1], data[2]);

                customers.add(customer);
                customerMap.put(data[0], customer);
            }
        }

    } catch (IOException e) {
        System.out.println("Error loading customers: " + e.getMessage());
    }
}

private void saveSales() {

    try (PrintWriter writer =
                 new PrintWriter(new FileWriter(SALES_FILE))) {

        for (Sale sale : sales) {

            writer.println(
                    sale.getTransactionId() + "|"
                    + sale.getCustomerId() + "|"
                    + sale.getCustomerName() + "|"
                    + sale.getDate() + "|"
                    + sale.getTotal() + "|"
                    + sale.getCashReceived() + "|"
                    + sale.getChange()
            );
        }

    } catch (IOException e) {
        System.out.println("Error saving sales: " + e.getMessage());
    }
}

private void loadSales() {

    File file = new File(SALES_FILE);

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

        String line;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split("\\|", -1);

            if (data.length == 7) {

                Sale sale = new Sale(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        Double.parseDouble(data[4]),
                        Double.parseDouble(data[5])
                );

                sales.add(sale);
            }
        }

    } catch (Exception e) {
        System.out.println("Error loading sales: " + e.getMessage());
    }
}

private void saveSaleItems() {

    try (PrintWriter writer =
                 new PrintWriter(new FileWriter(DETAILS_FILE))) {

        for (SaleItem item : saleItems) {

            writer.println(
                    item.getTransactionId() + "|"
                    + item.getProductId() + "|"
                    + item.getProductName() + "|"
                    + item.getQuantity() + "|"
                    + item.getPrice() + "|"
                    + item.getSubtotal()
            );
        }

    } catch (IOException e) {
        System.out.println("Error saving sale details: " + e.getMessage());
    }
}

private void loadSaleItems() {

    File file = new File(DETAILS_FILE);

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

        String line;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split("\\|", -1);

            if (data.length == 6) {

                SaleItem item = new SaleItem(
                        data[0],
                        data[1],
                        data[2],
                        Double.parseDouble(data[3]),
                        Double.parseDouble(data[4])
                );

                saleItems.add(item);
            }
        }

    } catch (Exception e) {
        System.out.println("Error loading sale details: " + e.getMessage());
    }
}

private void saveStockHistory() {

    try (PrintWriter writer =
                 new PrintWriter(new FileWriter(STOCK_FILE))) {

        for (String record : stockHistory) {
            writer.println(record);
        }

    } catch (IOException e) {
        System.out.println("Error saving stock history: " + e.getMessage());
    }
}

private void loadStockHistory() {

    File file = new File(STOCK_FILE);

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader =
                 new BufferedReader(new FileReader(file))) {

        String line;

        while ((line = reader.readLine()) != null) {
            stockHistory.add(line);
        }

    } catch (IOException e) {
        System.out.println("Error loading stock history: " + e.getMessage());
    }
}

public void saveAll() {

    saveProducts();
    saveCustomers();
    saveSales();
    saveSaleItems();
    saveStockHistory();
}
}

