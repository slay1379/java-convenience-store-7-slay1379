package store.service;

import store.domain.Product;
import store.domain.Promotion;

public class OrderService {
    private InventoryService inventoryService;
    private PromotionService promotionService;

    public OrderService(InventoryService inventoryService, PromotionService promotionService) {
        this.inventoryService = inventoryService;
        this.promotionService = promotionService;
    }

    public void processOrder(String productName, int quantity) {
        validateOrder(productName, quantity);
        Product product = inventoryService.getProduct(productName);
        Promotion promotion = product.getPromotion().orElse(null);
        int getQuantity = 0;
        if (promotion != null && promotionService.isPromotionApplicable(product)) {
            getQuantity = promotionService.calculateGetQuantity(quantity, promotion);
        }
        inventoryService.reduceStock(productName, quantity + getQuantity);
        int totalPrice = promotionService.calculateTotalPrice(product, quantity);
    }
}
