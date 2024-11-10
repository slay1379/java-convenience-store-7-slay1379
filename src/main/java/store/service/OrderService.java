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
        List<OrderItem> orderItems = createOrderItems(orderInput.split(","));
        if (orderInput.isEmpty()) {
            System.out.println("구매한 상품이 없습니다.");
            return;
        }
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

        String identifier = findAvailableProductIdentifier(productName, quantity);
        if (identifier == null) {
            System.out.println(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
            return null;
        }

        Product product = inventoryService.getProduct(identifier);
        int adjustQuantity = adjustQuantityForPromotion(product, quantity);
        if (adjustQuantity == -1) {
            return null;
        }

        inventoryService.reduceStock(identifier, adjustQuantity);
        int amount = product.getPrice() * adjustQuantity;
        return new OrderItem(identifier, productName, adjustQuantity, amount);
    }

    private int adjustQuantityForPromotion(Product product, int quantity) {
        int requiredQuantity = promotionService.getRequiredQuantityForPromotion(product, quantity);
        if (requiredQuantity > quantity) {
            System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
                    product.getName(), requiredQuantity - quantity);
            if (inputView.readYOrN().equalsIgnoreCase("Y")) {
                quantity = requiredQuantity;
            }
        }
        int availableStock = product.getStock();
        if (availableStock < quantity) {
            System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니가? (Y/N)",
                    product.getName(), quantity - availableStock);
            if (inputView.readYOrN().equalsIgnoreCase("Y")) {
                quantity = availableStock;
            } else {
                return -1;
            }
        }
        return quantity;
    }

    private String findAvailableProductIdentifier(String productName, int quantity) {
        List<Product> products = inventoryService.getProductsByName(productName);
        for (Product product : products) {
            if (product.getStock() >= quantity) {
                return product.getIdentifier();
            }
        }
        throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
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

    private void validateOrder(String identifier, int quantity) {
        Product product = inventoryService.getProduct(identifier);
        if (product == null || product.getStock() < quantity) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
        }
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
        Product product = inventoryService.getProduct(item.getProductIdentifier());
        int getQuantity = promotionService.calculateGetQuantity(product, item.getQuantity());
        if (getQuantity > 0) {
            inventoryService.reduceStock(product.getIdentifier(), getQuantity);
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
            Product product = inventoryService.getProduct(item.getProductIdentifier());
            int getQuantity = promotionService.calculateGetQuantity(product, item.getQuantity());
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
