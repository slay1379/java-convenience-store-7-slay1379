package store.service;

import store.domain.Product;
import store.domain.Promotion;
import store.domain.Receipt;
import store.exception.MessageConstants;

public class OrderService {
    private InventoryService inventoryService;
    private PromotionService promotionService;
    private Receipt receipt;

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
        createReceipt(product, quantity, getQuantity, totalPrice)
    }

    private void validateOrder(String productName, int quantity) {
        Product product = inventoryService.getProduct(productName);
        if (product == null) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.NULL_PRODUCT_EXCEPTION);
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
        }
    }

    private int calculateGetQuantity(Product product, int quantity) {
        Promotion promotion = product.getPromotion().orElse(null);
        if (promotion != null && promotionService.isPromotionApplicable(product)) {
            return promotionService.calculateGetQuantity(quantity, promotion);
        }
        return 0;
    }
}
