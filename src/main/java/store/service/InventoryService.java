package store.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;

public class InventoryService {
    private final Map<String, Product> products;

    public InventoryService(List<Product> productList) {
        products = new LinkedHashMap<>();
        addProducts(productList);
    }

    private void addProducts(List<Product> productList) {
        for (Product product : productList) {
            products.put(product.getIdentifier(), product);
        }
    }

    public Product getProduct(String identifier) {
        return products.get(identifier);
    }

    public void reduceStock(String identifier, int quantity) {
        Product product = getProduct(identifier);
        product.reduceStock(quantity);
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
}
