
import java.util.Scanner;

public class Main {

public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);

    MeatShopManager manager = new MeatShopManager();

    int choice;

    do {

        System.out.println("\n==================================================");
        System.out.println("              FRESH CUT MEAT SHOP");
        System.out.println("               MANAGEMENT SYSTEM");
        System.out.println("==================================================");

        System.out.println("[1] Product Management");
        System.out.println("[2] Inventory Management");
        System.out.println("[3] Customer Management");
        System.out.println("[4] New Customer Purchase");
        System.out.println("[5] Sales Records");
        System.out.println("[6] Sales Reports");
        System.out.println("[7] Low Stock Notifications");
        System.out.println("[8] Search Products");
        System.out.println("[0] Exit");

        System.out.println("--------------------------------------------------");

        System.out.print("Enter your choice: ");

        try {
            choice = Integer.parseInt(sc.nextLine());

        } catch (NumberFormatException e) {
            choice = -1;
        }

        switch (choice) {

            case 1:

                int productChoice;

                do {

                    System.out.println("\n========== PRODUCT MANAGEMENT ==========");
                    System.out.println("[1] Add New Product");
                    System.out.println("[2] View All Products");
                    System.out.println("[3] Update Product");
                    System.out.println("[4] Delete Product");
                    System.out.println("[0] Back to Main Menu");

                    productChoice = readChoice(sc);

                    switch (productChoice) {

                        case 1:
                            manager.addProduct(sc);
                            break;

                        case 2:
                            manager.viewAllProducts();
                            break;

                        case 3:
                            manager.updateProduct(sc);
                            break;

                        case 4:
                            manager.deleteProduct(sc);
                            break;

                        case 0:
                            break;

                        default:
                            System.out.println("Invalid choice!");
                    }

                } while (productChoice != 0);

                break;

            case 2:

                int inventoryChoice;

                do {

                    System.out.println("\n========== INVENTORY MANAGEMENT ==========");
                    System.out.println("[1] View Current Inventory");
                    System.out.println("[2] Restock Products");
                    System.out.println("[3] View Stock History");
                    System.out.println("[0] Back to Main Menu");

                    inventoryChoice = readChoice(sc);

                    switch (inventoryChoice) {

                        case 1:
                            manager.viewInventory();
                            break;

                        case 2:
                            manager.restockProduct(sc);
                            break;

                        case 3:
                            manager.viewStockHistory();
                            break;

                        case 0:
                            break;

                        default:
                            System.out.println("Invalid choice!");
                    }

                } while (inventoryChoice != 0);

                break;

            case 3:

                int customerChoice;

                do {

                    System.out.println("\n========== CUSTOMER MANAGEMENT ==========");
                    System.out.println("[1] Register New Customer");
                    System.out.println("[2] View All Customers");
                    System.out.println("[3] Search Customer");
                    System.out.println("[4] Update Customer");
                    System.out.println("[5] Delete Customer");
                    System.out.println("[0] Back to Main Menu");

                    customerChoice = readChoice(sc);

                    switch (customerChoice) {

                        case 1:
                            manager.registerCustomer(sc);
                            break;

                        case 2:
                            manager.viewAllCustomers();
                            break;

                        case 3:
                            manager.searchCustomer(sc);
                            break;

                        case 4:
                            manager.updateCustomer(sc);
                            break;

                        case 5:
                            manager.deleteCustomer(sc);
                            break;

                        case 0:
                            break;

                        default:
                            System.out.println("Invalid choice!");
                    }

                } while (customerChoice != 0);

                break;

            case 4:
                manager.newPurchase(sc);
                break;

            case 5:
                manager.viewSalesRecords();
                break;

            case 6:
                manager.salesReport();
                break;

            case 7:
                manager.lowStockAlert();
                break;

            case 8:
                manager.searchProduct(sc);
                break;

            case 0:
                manager.saveAll();
                System.out.println("\nThank you for using Fresh Cut Meat Shop!");
                System.out.println("Have a great day!");
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
        }

    } while (choice != 0);

    sc.close();
}

private static int readChoice(Scanner sc) {

    try {
        System.out.print("Enter your choice: ");
        return Integer.parseInt(sc.nextLine());

    } catch (NumberFormatException e) {
        return -1;
    }
}
}
