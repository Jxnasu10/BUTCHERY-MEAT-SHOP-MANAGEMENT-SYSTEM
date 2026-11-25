
public class Fish extends Product {

public Fish(String productId, String productName,
            double price, double stock) {

    super(productId, productName, price, stock);
}

@Override
public String getCategory() {
    return "Fish";
}
}
