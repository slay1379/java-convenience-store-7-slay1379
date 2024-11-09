package store.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;

public class InventoryService {
    private Map<String, Product> products;

    public InventoryService(List<Product> productList) {
        products = new HashMap<>();
        addProducts(productList);
    }

    private void addProducts(List<Product> productList) {
        for (Product product : productList) {
            products.put(product.getName(), product);
        }
    }

    public Product getProduct(String name) {
        return products.get(name);
    }

    public void reduceStock(String name, int quantity) {
        Product product = getProduct(name);
        product.reduceStock(quantity);
    }

    public Map<String, Product> getProducts() {
        return products;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
}
