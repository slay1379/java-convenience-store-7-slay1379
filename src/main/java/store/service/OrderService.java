package store.service;

import java.util.ArrayList;
import java.util.List;
import store.domain.GiftItem;
import store.domain.OrderItem;
import store.domain.Product;
import store.domain.Receipt;
import store.exception.MessageConstants;
import store.view.InputView;

public class OrderService {
    private InventoryService inventoryService;
    private PromotionService promotionService;
    private InputView inputView;
    private Receipt receipt;

    public OrderService(InventoryService inventoryService, PromotionService promotionService, InputView inputView) {
        this.inventoryService = inventoryService;
        this.promotionService = promotionService;
        this.inputView = inputView;
    }

    public void processOrder(String orderInput, boolean isMember) {
        if (orderInput.isEmpty()) {
            System.out.println("구매한 상품이 없습니다.");
            return;
        }
        List<OrderItem> orderItems = createOrderItems(orderInput.split(","));
        List<GiftItem> giftItems = createGiftItems(orderItems);

        int totalAmount = calculateTotalAmount(orderItems);
        int promotionDiscount = calculatePromotionDiscount(orderItems);
        int amountAfterPromotion = totalAmount - promotionDiscount;
        int membershipDiscount = calculateMembershipDiscount(amountAfterPromotion, isMember);
        int finalAmount = amountAfterPromotion - membershipDiscount;

        receipt = new Receipt(orderItems, giftItems, totalAmount, promotionDiscount, membershipDiscount, finalAmount);
    }

    private List<OrderItem> createOrderItems(String[] orders) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (String order : orders) {
            OrderItem item = createOrderItem(order);
            if (item != null) {
                orderItems.add(item);
            }
        }
        return orderItems;
    }

    private OrderItem createOrderItem(String order) {
        String[] details = extractOrderDetails(order);
        String productName = details[0];
        int quantity = Integer.parseInt(details[2]);

        List<Product> products = inventoryService.getProductsByName(productName);

        int remainingQuantity = quantity;
        int totalAmount = 0;
        int usedPromotionStock = 0;

        for (Product product : products) {
            if (remainingQuantity <= 0) {
                break;
            }

            int availableStock = product.getPromotionStock() + product.getRegularStock();
            if (availableStock > 0) {
                usedPromotionStock = Math.min(product.getPromotionStock(), remainingQuantity);
                product.reduceStock(usedPromotionStock, true);
                remainingQuantity -= usedPromotionStock;

                int usedRegularStock = Math.min(product.getRegularStock(), remainingQuantity);
                product.reduceStock(usedRegularStock, false);
                remainingQuantity -= usedRegularStock;

                totalAmount += product.getPrice() * quantity;
            }
        }
        if (remainingQuantity > 0) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
        }

        return new OrderItem(productName, quantity, totalAmount, usedPromotionStock);
    }

    private String[] extractOrderDetails(String order) {
        String content = order.substring(1, order.length() - 1);
        String[] details = content.split("-");
        if (details.length == 2) {
            return new String[]{details[0], "null", details[1]};
        }
        if (details.length == 3) {
            return details;
        }
        throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION);
    }

    private List<GiftItem> createGiftItems(List<OrderItem> orderItems) {
        List<GiftItem> giftItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            GiftItem gift = createGiftItem(item);
            if (gift != null) {
                giftItems.add(gift);
            }
        }
        return giftItems;
    }

    private GiftItem createGiftItem(OrderItem item) {
        Product product = inventoryService.getProduct(item.getProductName());
        int getQuantity = promotionService.calculateGetQuantity(product, item.getUsedPromotionStock());
        if (getQuantity > 0) {
            return new GiftItem(product.getName(), getQuantity);
        }
        return null;
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
            Product product = inventoryService.getProduct(item.getProductName());
            int getQuantity = promotionService.calculateGetQuantity(product, item.getUsedPromotionStock());
            discount += getQuantity * product.getPrice();
        }
        return discount;
    }

    private int calculateMembershipDiscount(int amountAfterPromotion, boolean isMember) {
        if (!isMember) {
            return 0;
        }
        int discount = (amountAfterPromotion * 25) / 100;
        return Math.min(discount, 8000);
    }

    public Receipt getReceipt() {
        return receipt;
    }
}
