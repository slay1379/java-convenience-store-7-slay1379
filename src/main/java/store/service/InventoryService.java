package store.service;

import java.util.ArrayList;
import java.util.LinkedHashMap; // 변경된 부분
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.exception.MessageConstants;

public class InventoryService {
    private Map<String, Product> inventory;

    public InventoryService(List<Product> products) {
        this.inventory = new LinkedHashMap<>(); // 변경된 부분
        for (Product product : products) {
            this.inventory.put(product.getIdentifier(), product);
        }
    }

    public Product getProduct(String identifier) {
        return inventory.get(identifier);
    }

    public List<Product> getProductsByName(String name) {
        List<Product> products = new ArrayList<>();
        for (Product product : inventory.values()) {
            if (product.getName().equals(name)) {
                products.add(product);
            }
        }
        return products;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }

    public void reduceStock(String identifier, int quantity) {
        Product product = getProduct(identifier);
        if (product == null) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.NULL_PRODUCT_EXCEPTION);
        }
        product.reduceStock(quantity);
    }
}
