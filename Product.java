public abstract class Product {

private String productId;
private String productName;
private double price;
private double stock;

public Product(String productId, String productName,
               double price, double stock) {

    this.productId = productId;
    this.productName = productName;
    this.price = price;
    this.stock = stock;
}

public String getProductId() {
    return productId;
}

public String getProductName() {
    return productName;
}

public double getPrice() {
    return price;
}

public double getStock() {
    return stock;
}

public void setProductName(String productName) {
    this.productName = productName;
}

public void setPrice(double price) {
    this.price = price;
}

public void setStock(double stock) {
    this.stock = stock;
}

public abstract String getCategory();

public void displayProduct() {
    System.out.printf("%-8s %-20s %-12s ₱%-10.2f %-10.2f kg%n",
            productId,
            productName,
            getCategory(),
            price,
            stock);
}
}
