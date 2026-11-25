
public class Seafood extends Product {

public Seafood(String productId, String productName,
               double price, double stock) {

    super(productId, productName, price, stock);
}

@Override
public String getCategory() {
    return "Seafood";
}
}
