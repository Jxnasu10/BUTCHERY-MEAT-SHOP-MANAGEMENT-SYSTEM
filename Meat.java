
public class Meat extends Product {

public Meat(String productId, String productName,
            double price, double stock) {

    super(productId, productName, price, stock);
}

@Override
public String getCategory() {
    return "Meat";
}
}
