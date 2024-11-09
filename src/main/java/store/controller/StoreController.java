package store.controller;

import java.util.List;
import store.domain.Product;
import store.domain.Promotion;
import store.exception.MessageConstants;
import store.service.InventoryService;
import store.service.OrderService;
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
        this.orderService = new OrderService(inventoryService, promotionService);
        this.inputView = new InputView();
        this.outputView = new OutputView();
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
            boolean isMember = askMembershipDiscount();
            orderService.processOrder(orderInput, isMember);
            outputView.printReceipt(orderService.getReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println(MessageConstants.ERROR + e.getMessage());
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
