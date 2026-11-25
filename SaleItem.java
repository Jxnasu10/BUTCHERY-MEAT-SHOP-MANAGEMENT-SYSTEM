
public class SaleItem {

private String transactionId;
private String productId;
private String productName;
private double quantity;
private double price;
private double subtotal;

public SaleItem(String transactionId, String productId,
                String productName, double quantity,
                double price) {

    this.transactionId = transactionId;
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.price = price;
    this.subtotal = quantity * price;
}

public String getTransactionId() {
    return transactionId;
}

public String getProductId() {
    return productId;
}

public String getProductName() {
    return productName;
}

public double getQuantity() {
    return quantity;
}

public double getPrice() {
    return price;
}

public double getSubtotal() {
    return subtotal;
}
}
