
public class Sale {

private String transactionId;
private String customerId;
private String customerName;
private String date;
private double total;
private double cashReceived;
private double change;

public Sale(String transactionId, String customerId,
            String customerName, String date,
            double total, double cashReceived) {

    this.transactionId = transactionId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.date = date;
    this.total = total;
    this.cashReceived = cashReceived;
    this.change = cashReceived - total;
}

public String getTransactionId() {
    return transactionId;
}

public String getCustomerId() {
    return customerId;
}

public String getCustomerName() {
    return customerName;
}

public String getDate() {
    return date;
}

public double getTotal() {
    return total;
}

public double getCashReceived() {
    return cashReceived;
}

public double getChange() {
    return change;
}

public void displaySale() {
    System.out.printf("%-10s %-22s %-12s ₱%-12.2f%n",
            transactionId,
            customerName,
            date,
            total);
}
}
