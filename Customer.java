public class Customer {

private String customerId;
private String name;
private String contactNumber;

public Customer(String customerId, String name,
                String contactNumber) {

    this.customerId = customerId;
    this.name = name;
    this.contactNumber = contactNumber;
}

public String getCustomerId() {
    return customerId;
}

public String getName() {
    return name;
}

public String getContactNumber() {
    return contactNumber;
}

public void setName(String name) {
    this.name = name;
}

public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
}

public void displayCustomer() {
    System.out.printf("%-10s %-25s %-15s%n",
            customerId, name, contactNumber);
}
}
