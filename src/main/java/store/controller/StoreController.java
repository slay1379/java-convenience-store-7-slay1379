package store.controller;

import java.util.List;
import store.domain.Product;
import store.domain.Promotion;
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
    }

    private void displayProducts() {
        List<Product> products = inventoryService.getAllProducts();
        outputView.printAllProducts(products);
    }

    private void processSingleOrder() {
        String orderInput = inputView.readProduct();
        try {

        }
    }

    private boolean askMembershipDiscount() {
        String membershipInput = inputView.readMembershipDiscount();
        return membershipInput.equalsIgnoreCase("Y");
    }
}
