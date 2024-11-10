package store.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Order;
import store.domain.GiftItem;
import store.domain.OrderItem;
import store.domain.Product;
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
        List<OrderItem> orderItems = createOrderItems(orderInput.split(","));
        List<GiftItem> giftItems = createGiftItems(orderItems);

        int totalAmount = calculateTotalAmount(orderItems);
        int promotionDiscount = calculatePromotionDiscount(orderItems);
        int amountAfterPromotion = totalAmount - promotionDiscount;
        int membershipDiscount = calculateMembershipDiscount(orderItems, isMember);
        int finalAmount = amountAfterPromotion - membershipDiscount;

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

    private OrderItem createOrderItem(String order) {
        String[] details = extractOrderDetails(order);
        String productName = details[0];
        int quantity = Integer.parseInt(details[2]);

        // 프로모션 상품 먼저 확인
        String promotionIdentifier = generateIdentifier(productName, "탄산2+1");
        Product promotionProduct = inventoryService.getProduct(promotionIdentifier);

        // 프로모션 상품이 있고 재고가 충분하면 프로모션 상품으로 처리
        if (promotionProduct != null && promotionProduct.getStock() >= quantity) {
            validateOrder(promotionIdentifier, quantity);
            inventoryService.reduceStock(promotionIdentifier, quantity);
            return new OrderItem(promotionIdentifier, productName, quantity,
                    promotionProduct.getPrice() * quantity);
        }

        // 프로모션 상품이 없거나 재고가 부족하면 일반 상품으로 처리
        String regularIdentifier = productName;
        validateOrder(regularIdentifier, quantity);
        Product regularProduct = inventoryService.getProduct(regularIdentifier);
        inventoryService.reduceStock(regularIdentifier, quantity);
        return new OrderItem(regularIdentifier, productName, quantity,
                regularProduct.getPrice() * quantity);
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

    private String getPromotionName(String promotionField) {
        if ("null".equalsIgnoreCase(promotionField)) {
            return null;
        }
        return promotionField;
    }

    private String generateIdentifier(String productName, String promotionName) {
        if (promotionName != null) {
            return productName + "_" + promotionName;
        }
        return productName;
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

    private List<GiftItem> createGiftItems(List<OrderItem> orderItems) {
        List<GiftItem> giftItems = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Product product = inventoryService.getProduct(item.getProductIdentifier());
            // 프로모션 적용 가능 여부 확인
            if (product != null && product.getPromotion().isPresent() &&
                    promotionService.isPromotionApplicable(product)) {
                int getQuantity = promotionService.calculateGetQuantity(product, item.getQuantity());
                if (getQuantity > 0) {
                    giftItems.add(new GiftItem(product.getName(), getQuantity));
                }
            }
        }
        return giftItems;
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
            if (product != null && product.getPromotion().isPresent() &&
                    promotionService.isPromotionApplicable(product)) {
                int getQuantity = promotionService.calculateGetQuantity(product, item.getQuantity());
                discount += getQuantity * product.getPrice();
            }
        }
        return discount;
    }

    private int calculateMembershipDiscount(List<OrderItem> orderItems, boolean isMember) {
        if (!isMember) {
            return 0;
        }

        // 프로모션이 적용되지 않은 상품만 필터링
        int nonPromotionAmount = 0;
        for (OrderItem item : orderItems) {
            Product product = inventoryService.getProduct(item.getProductIdentifier());
            if (product != null && (!product.getPromotion().isPresent() ||
                    !promotionService.isPromotionApplicable(product))) {
                nonPromotionAmount += item.getAmount();
            }
        }

        // 멤버십 할인 적용 (할인율 30%, 최대 8,000원)
        double discountRate = 0.3;
        int maxDiscount = 8000;
        int calculatedDiscount = (int) (nonPromotionAmount * discountRate);
        return Math.min(calculatedDiscount, maxDiscount);
    }

    public Receipt getReceipt() {
        return receipt;
    }
}
