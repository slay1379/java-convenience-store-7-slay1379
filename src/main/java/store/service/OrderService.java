package store.service;

import java.util.ArrayList;
import java.util.List;
import store.domain.GiftItem;
import store.domain.OrderItem;
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

    public void processOrder(String orderInput, boolean isMember) {
        String[] orders = orderInput.split(",");
        List<OrderItem> orderItems = createOrderItems(orders);
        List<GiftItem> giftItems = createGiftItems(orderItems);

        int totalAmount = calculateTotalAmount(orderItems);
        int promotionDiscount = calculatePromotionDiscount(orderItems);
        int membershipDiscount = calculateMembershipDiscount(totalAmount, isMember);
        int finalAmount = totalAmount - promotionDiscount - membershipDiscount;

        receipt = new Receipt(orderItems, giftItems, totalAmount, promotionDiscount, membershipDiscount, finalAmount);
    }

    private List<OrderItem> createOrderItems(String[] orders) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (String order : orders) {
            OrderItem item = createOrderItem(order);
            orderItems.add(item);
        }
        return orderItems;
    }

    private String getPromotionName(String promotionField) {
        if ("null".equalsIgnoreCase(promotionField)) {
            return null;
        }
        return promotionField;
    }

    private OrderItem createOrderItem(String order) {
        String[] details = extractOrderDetails(order);
        String productName = details[0];
        String promotionName = getPromotionName(details[1]);
        int quantity = Integer.parseInt(details[2]);

        String identifier = generateIdentifier(productName, promotionName);
        validateOrder(identifier, quantity);
        Product product = inventoryService.getProduct(identifier);
        inventoryService.reduceStock(identifier, quantity);

        int amount = product.getPrice() * quantity;
        return new OrderItem(identifier, productName, quantity, amount);
    }

    private String generateIdentifier(String productName, String promotionName) {
        if (promotionName != null) {
            return productName + "_" + promotionName;
        }
        return productName;
    }

    private String[] extractOrderDetails(String order) {
        String content = order.substring(1, order.length() - 1);
        String[] details = content.split("-");
        if (details.length != 3) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION);
        }
        return details;
    }

    private List<GiftItem> createGiftItems(List<OrderItem> orderItems) {
        List<GiftItem> giftItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            GiftItem gift = createGiftItem(item);
        }
        return giftItems;
    }

    private GiftItem createGiftItem(OrderItem item) {
        Product product = inventoryService.getProduct(item.getProductIdentifier());
        int getQuantity = calculateGetQuantity(product, item.getQuantity());
        if (getQuantity > 0) {
            inventoryService.reduceStock(product.getIdentifier(), getQuantity);
            return new GiftItem(product.getName(), getQuantity);
        }
        return null;
    }

    private void validateOrder(String identifier, int quantity) {
        Product product = inventoryService.getProduct(identifier);
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

    private int calculateTotalAmount(List<OrderItem> orderItems) {
        int total = 0;
        for (OrderItem item : orderItems) {
            total += item.getAmount();
        }
        return total;
    }

    private int calculatePromotionDiscount(List<OrderItem> orderItems) {
        int discount = 0;
        for (OrderItem item : orderItems) {
            Product product = inventoryService.getProduct(item.getProductIdentifier());
            int getQuantity = calculateGetQuantity(product, item.getQuantity());
            discount += getQuantity * product.getPrice();
        }
        return discount;
    }

    private int calculateMembershipDiscount(int amount, boolean isMember) {
        if (!isMember) {
            return 0;
        }
        int discount = (int) (amount * 0.1);
        return Math.min(discount, 10000);
    }

    public Receipt getReceipt() {
        return receipt;
    }
}
