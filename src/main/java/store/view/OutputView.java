package store.view;

import java.util.List;
import store.domain.GiftItem;
import store.domain.OrderItem;
import store.domain.Product;
import store.domain.Receipt;

public class OutputView {

    private static final String GREETING_MESSAGE = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.";

    public void printAllProducts(List<Product> products) {
        System.out.println(GREETING_MESSAGE);
        for (Product product : products) {
            printProduct(product);
        }
    }

    private void printProduct(Product product) {
        String stockInfo = getStockInfo(product.getStock());
        String promotionInfo = getPromotionInfo(product);
        System.out.println("- " + product.getName() + product.getPrice() + "원 " + stockInfo + promotionInfo + "\n");
    }

    private String getStockInfo(int stock) {
        if (stock > 0) {
            return stock + "개";
        }
        return "재고 없음";
    }

    private String getPromotionInfo(Product product) {
        return product.getPromotion().map(promotion -> " " + promotion.getName()).orElse("");
    }

    public void printReceipt(Receipt receipt) {
        System.out.println("=========== 영수증 ===========");
        printOrderItems(receipt.getOrderItems());
        printGiftItems(receipt.getGiftItems());
        printTotals(receipt);
    }

    private void printOrderItems(List<OrderItem> items) {
        System.out.println("상품명\t수량\t금액");
        for (OrderItem item : items) {
            System.out.println(item.getProductName() + "\t" + item.getQuantity() + "\t" + item.getAmount());
        }
    }

    private void printGiftItems(List<GiftItem> gifts) {
        if (gifts.isEmpty()) {
            return;
        }
        System.out.println("\n증정상품\t수량");
        for (GiftItem gift : gifts) {
            System.out.println(gift.getProductName()+"\t"+gift.getQuantity()+"\n");
        }
    }

    private void printTotals(Receipt receipt) {
        System.out.println("----------------------------");
        System.out.println("총 구매액:\t" + receipt.getTotalAmount() + "원\n");
        if (receipt.getPromotionDiscount() > 0) {
            System.out.println("프로모션 할인:\t-%"+receipt.getPromotionDiscount()+"원\n");
        }
        if (receipt.getMembershipDiscount() > 0) {
            System.out.println("멤버십 할인:\t-"+receipt.getMembershipDiscount()+"원\n");
        }
        System.out.println("----------------------------");
        System.out.println("내실돈:\t\t"+receipt.getFinalAmount()+"원\n");
    }
}
