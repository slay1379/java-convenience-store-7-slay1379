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
        OrderValidationResult validationResult = getValidOrder();
        if (validationResult.isValid()) {
            boolean isMember = askMembershipDiscount();
            orderService.processOrder(validationResult.getOrderInput(), isMember, validationResult);
            outputView.printReceipt(orderService.getReceipt());
        }
    }

    private OrderValidationResult getValidOrder() {
        while (true) {
            try {
                String orderInput = inputView.readProduct();
                OrderValidationResult validationResult = orderService.validateOrder(orderInput);

                if (!validationResult.isValid()) {
                    if (orderInput.isEmpty()) {
                        System.out.println(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION
                                + MessageConstants.RE_INPUT);
                        continue;
                    }
                    System.out.println(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION);
                    continue;
                }

                return validationResult;
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains(MessageConstants.PATTERN_EXCEPTION)) {
                    System.out.println(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION);
                } else if (e.getMessage().contains(MessageConstants.NOT_EXIST_PRODUCT_EXCEPTION + MessageConstants.RE_INPUT)) {
                    System.out.println(MessageConstants.ERROR + MessageConstants.NOT_EXIST_PRODUCT_EXCEPTION + MessageConstants.RE_INPUT);
                } else if (e.getMessage().contains(MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION)) {
                    System.out.println(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION + MessageConstants.RE_INPUT);
                } else {
                    System.out.println(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION + MessageConstants.RE_INPUT);
                }
            }
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
