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
        for (Product product : inventory.values()) {
            if (product.getName().equals(name)) {
                products.add(product);
            }
        }

        products.sort((p1, p2) -> {
            if (p1.getPromotion().isPresent() && !p2.getPromotion().isPresent()) {
                return -1;
            } else if (!p1.getPromotion().isPresent() && p2.getPromotion().isPresent()) {
                return 1;
            }
            return 0;
        });
        return products;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }

    public void reduceStock(String identifier, int quantity, boolean usePromotionStock) {
        Product product = getProduct(identifier);
        if (product == null) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.NULL_PRODUCT_EXCEPTION);
        }
        product.reduceStock(quantity, usePromotionStock);
    }

}
