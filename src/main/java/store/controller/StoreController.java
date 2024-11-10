package store.controller;

import java.util.List;
import store.domain.Product;
import store.domain.Promotion;
import store.exception.MessageConstants;
import store.service.InventoryService;
import store.service.OrderService;
import store.service.OrderService.OrderValidationResult;
import store.service.PromotionService;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {
    private InventoryService inventoryService;
    private PromotionService promotionService;
    private OrderService orderService;
    private final InputView inputView;
    private final OutputView outputView;

    public StoreController(List<Product> products, List<Promotion> promotions) {
        this.inventoryService = new InventoryService(products);
        this.promotionService = new PromotionService(promotions);
        this.inputView = new InputView();
        this.outputView = new OutputView();
        this.orderService = new OrderService(inventoryService, promotionService,inputView);
    }

    public void run() {
        boolean continueShopping = true;
        while (continueShopping) {
            displayProducts();
            processSingleOrder();
            continueShopping = askContinueShopping();
        }
    }

    private void displayProducts() {
        List<Product> products = inventoryService.getAllProducts();
        outputView.printAllProducts(products);
    }

    private void processSingleOrder() {
        String orderInput = inputView.readProduct();
        try {
            // 주문 유효성 검사 및 프로모션 재고 확인
            OrderValidationResult validationResult = orderService.validateOrder(orderInput);

            if (validationResult.isValid()) {
                // 멤버십 할인 확인
                boolean isMember = askMembershipDiscount();
                // 최종 주문 처리
                orderService.processOrder(orderInput, isMember, validationResult);
                outputView.printReceipt(orderService.getReceipt());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean askMembershipDiscount() {
        String membershipInput = inputView.readMembershipDiscount();
        return membershipInput.equalsIgnoreCase("Y");
    }

    private boolean askContinueShopping() {
        String continueInput = inputView.readPurchaseOtherProduct();
        return continueInput.equalsIgnoreCase("Y");
    }
}
