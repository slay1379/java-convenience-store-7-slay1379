package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.domain.GiftItem;
import store.domain.OrderItem;
import store.domain.Product;
import store.domain.Promotion;
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

    public static class OrderValidationResult {
        private final boolean isValid;
        private final List<OrderItem> orderItems;
        private final String orderInput;

        public OrderValidationResult(boolean isValid, List<OrderItem> orderItems, String orderInput) {
            this.isValid = isValid;
            this.orderItems = orderItems;
            this.orderInput = orderInput;
        }

        public boolean isValid() {
            return isValid;
        }

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public String getOrderInput() {
            return orderInput;
        }
    }

    public OrderValidationResult validateOrder(String orderInput) {
        validateOrderInput(orderInput);
        String[] orders = parseOrders(orderInput);
        validateOrders(orders);
        List<OrderItem> orderItems = createValidOrderItems(orders);
        return new OrderValidationResult(true, orderItems, orderInput);
    }

    private void validateOrderInput(String orderInput) {
        if (orderInput == null || orderInput.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.PATTERN_EXCEPTION + MessageConstants.RE_INPUT);
        }
    }

    private String[] parseOrders(String orderInput) {
        return orderInput.trim().split(",");
    }

    private void validateOrders(String[] orders) {
        for (String order : orders) {
            validateSingleOrder(order);
        }
    }

    private List<OrderItem> createValidOrderItems(String[] orders) {
        List<OrderItem> orderItems = createOrderItems(orders);
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.PATTERN_EXCEPTION + MessageConstants.RE_INPUT);
        }
        return orderItems;
    }

    private void validateSingleOrder(String order) {
        if (!order.startsWith("[") || !order.endsWith("]")) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.PATTERN_EXCEPTION);
        }

        String[] details = extractOrderDetails(order);
        String productName = details[0];

        // 상품 존재 여부 확인
        List<Product> products = inventoryService.getProductsByName(productName);
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.NOT_EXIST_PRODUCT_EXCEPTION + MessageConstants.RE_INPUT);
        }

        // 수량 형식 확인
        try {
            int quantity = Integer.parseInt(details[2]);
            if (quantity <= 0) {
                throw new IllegalArgumentException(MessageConstants.ERROR +
                        MessageConstants.PATTERN_EXCEPTION + MessageConstants.RE_INPUT);
            }

            // 재고 수량 확인
            int totalAvailableStock = products.stream()
                    .mapToInt(p -> p.getRegularStock() + p.getPromotionStock())
                    .sum();
            if (quantity > totalAvailableStock) {
                throw new IllegalArgumentException(MessageConstants.ERROR +
                        MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.PATTERN_EXCEPTION);
        }
    }

    public void processOrder(String orderInput, boolean isMember, OrderValidationResult validationResult) {
        List<OrderItem> orderItems = validationResult.getOrderItems();
        List<GiftItem> giftItems = createGiftItems(orderItems);

        int totalAmount = calculateTotalAmount(orderItems);
        int promotionDiscount = calculatePromotionDiscount(orderItems);
        int amountAfterPromotion = totalAmount - promotionDiscount;
        int membershipDiscount = calculateMembershipDiscount(orderItems, isMember);
        int finalAmount = amountAfterPromotion - membershipDiscount;

        receipt = new Receipt(orderItems, giftItems, totalAmount, promotionDiscount,
                membershipDiscount, finalAmount);
    }

    private List<OrderItem> createOrderItems(String[] orders) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (String order : orders) {
            OrderItem item = createOrderItem(order);
            if (item == null) {
                return null;
            }
            orderItems.add(item);
        }
        return orderItems;
    }

    private OrderItem createOrderItem(String order) {
        String[] details = extractOrderDetails(order);
        String productName = details[0];
        int quantity = Integer.parseInt(details[2]);

        List<Product> products = inventoryService.getProductsByName(productName);

        // 프로모션이 없는 상품인 경우 바로 일반 재고에서 처리
        if (!hasPromotion(products) || !hasValidPromotion(products)) {
            return createNonPromotionalOrderItem(products, productName, quantity);
        }

        return createPromotionalOrderItem(products, productName, quantity);
    }

    private boolean hasPromotion(List<Product> products) {
        return products.stream()
                .anyMatch(product -> product.getPromotion().isPresent());
    }

    private OrderItem createNonPromotionalOrderItem(List<Product> products, String productName, int quantity) {
        int totalStock = products.stream()
                .mapToInt(Product::getRegularStock)
                .sum();

        if (quantity > totalStock) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
        }

        int remainingQuantity = quantity;
        int totalAmount = 0;

        for (Product product : products) {
            if (remainingQuantity <= 0) {
                break;
            }

            int usedStock = Math.min(product.getRegularStock(), remainingQuantity);
            product.reduceStock(usedStock, false);
            remainingQuantity -= usedStock;
            totalAmount += product.getPrice() * usedStock;
        }

        return new OrderItem(productName, quantity, totalAmount, 0);
    }

    private OrderItem createPromotionalOrderItem(List<Product> products, String productName, int quantity) {
        Product promotionalProduct = products.stream()
                .filter(p -> p.getPromotion().isPresent())
                .findFirst()
                .orElseThrow();

        if (promotionalProduct.getPromotionStock() > quantity) {
            // 프로모션으로 추가로 받을 수 있는 수량 확인
            int additionalFreeQuantity = promotionService.calculateAdditionalFreeQuantity(promotionalProduct, quantity);
            if (additionalFreeQuantity > 0) {
                String addResponse = inputView.confirmAdditionalPromotion(productName, additionalFreeQuantity);
                if (addResponse.equalsIgnoreCase("Y")) {
                    // 구매 수량 업데이트
                    quantity += additionalFreeQuantity;
                }
            }
        }

        int remainingQuantity = quantity;
        int totalAmount = 0;
        int usedPromotionStock = 0;

        for (Product product : products) {
            if (remainingQuantity <= 0) {
                break;
            }

            if (product.getRegularStock() + product.getPromotionStock() < quantity) {
                throw new IllegalArgumentException(MessageConstants.ERROR +
                        MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
            }

            int availableStock = product.getPromotionStock() + product.getRegularStock();
            if (availableStock > 0) {
                // 프로모션 재고 사용
                usedPromotionStock = Math.min(product.getPromotionStock(), remainingQuantity);
                remainingQuantity -= usedPromotionStock;

                // 일반 재고로 처리해야 할 수량이 있는 경우
                int usedRegularStock = Math.min(product.getRegularStock(), remainingQuantity);
                int remainingWithoutPromotion = quantity - usedPromotionStock;
                if (quantity >
                        product.getPromotionStock() / (product.getPromotion().get().getBuy() + product.getPromotion()
                                .get()
                                .getGet()) * (product.getPromotion().get().getBuy() + product.getPromotion().get()
                                .getGet())) {
                    int exceptionPromotion = remainingWithoutPromotion + product.getPromotionStock() % (
                            product.getPromotion().get().getBuy()
                                    + product.getPromotion().get().getGet());
                    String response = inputView.confirmPartialPromotion(exceptionPromotion, productName);
                    if (!response.equalsIgnoreCase("Y")) {
                        if (exceptionPromotion < usedRegularStock) {
                            usedRegularStock -= exceptionPromotion;
                        }
                        if (exceptionPromotion > usedRegularStock) {
                            usedPromotionStock -= (exceptionPromotion - usedRegularStock);
                            usedRegularStock = 0;
                        }
                        quantity -= exceptionPromotion;
                    }
                }

                // 재고 감소 및 금액 계산
                product.reduceStock(usedPromotionStock, true);
                product.reduceStock(usedRegularStock, false);
                // 전체 수량(원래 수량 + 추가된 수량)에 대해 금액 계산
                totalAmount += product.getPrice() * quantity;
                remainingQuantity -= usedRegularStock;
            }
        }

        // 전체 수량(원래 수량 + 추가된 수량)을 OrderItem에 반영
        return new OrderItem(productName, quantity, totalAmount, usedPromotionStock);
    }

    private String[] extractOrderDetails(String order) {
        try {
            String content = order.substring(1, order.length() - 1);
            String[] details = content.split("-");
            if (details.length == 2) {
                return new String[]{details[0], "null", details[1]};
            }
            if (details.length == 3) {
                return details;
            }
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.PATTERN_EXCEPTION);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(MessageConstants.ERROR +
                    MessageConstants.PATTERN_EXCEPTION);
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

    private int calculateMembershipDiscount(List<OrderItem> orderItems, boolean isMember) {
        if (!isMember) {
            return 0;
        }
        int discount = 0;
        for (OrderItem item : orderItems) {
            Product product = inventoryService.getProduct(item.getProductName());
            if (!product.getPromotion().isPresent()) {
                discount += (item.getAmount() * 30) / 100;
            }
        }
        return discount;
    }

    private boolean hasValidPromotion(List<Product> products) {
        return products.stream()
                .anyMatch(product ->
                        product.getPromotion().isPresent() &&
                                isPromotionValid(product.getPromotion().get()));
    }

    private boolean isPromotionValid(Promotion promotion) {
        LocalDate now = DateTimes.now().toLocalDate();
        return !now.isBefore(promotion.getStartDate()) &&
                !now.isAfter(promotion.getEndDate());
    }

    public Receipt getReceipt() {
        return receipt;
    }


}