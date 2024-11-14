package store.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.exception.MessageConstants;

public class InventoryService {
    private Map<String, Product> inventory;

    public InventoryService(List<Product> products) {
        this.inventory = new LinkedHashMap<>();
        for (Product product : products) {
            this.inventory.put(product.getName(), product);
        }
    }

    public Product getProduct(String identifier) {
        return inventory.get(identifier);
    }

    public List<Product> getProductsByName(String name) {
        List<Product> products = new ArrayList<>();
        inventory.values().stream()
                .filter(product -> product.getName().equals(name))
                .forEach(products::add);
        products.sort((p1, p2) -> Boolean.compare(p2.getPromotion().isPresent(), p1.getPromotion().isPresent()));
        return products;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }
}
