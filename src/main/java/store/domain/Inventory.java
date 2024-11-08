package store.domain;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Product> products = new HashMap<>();
    private Map<String, Promotion> promotions = new HashMap<>();

    public Product getProduct(String name) {
        return products.get(name);
    }

    public Map<String,Product> getAllProduct() {
        return products;
    }

    public Promotion getPromotion(String name) {
        return promotions.get(name);
    }
}
