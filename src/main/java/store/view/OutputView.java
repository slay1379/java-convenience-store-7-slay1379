package store.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.domain.GiftItem;
import store.domain.OrderItem;
import store.domain.Product;
import store.domain.Receipt;

public class OutputView {

    private static final String GREETING_MESSAGE = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.";

    public void printAllProducts(List<Product> products) {
        System.out.println(GREETING_MESSAGE);

        // 이름으로 제품을 그룹화하면서 입력된 순서를 유지합니다.
        Map<String, List<Product>> productsByName = products.stream()
                .collect(Collectors.groupingBy(Product::getName, LinkedHashMap::new, Collectors.toList()));

        for (List<Product> productList : productsByName.values()) {
            Product product = productList.get(0);
            boolean hasPromotion = product.getPromotion().isPresent();
            boolean hasPrintedPromotion = false;
            if (product.getPromotionStock() > 0) {
                printProductWithPromotion(product);
                hasPrintedPromotion = true;
            } else if (hasPromotion) {
                printNoPromotionStockWithPromotion(product);
            }
            if (product.getRegularStock() > 0) {
                printProductWithRegularStock(product);
            } else {
                printNoRegularStockWithPromotion(product);
            }
        }
    }

    private void printProductWithPromotion(Product product) {
        String stockInfo = product.getPromotionStock() + "개";
        String promotionInfo = getPromotionInfo(product);
        String priceWithComma = String.format("%,d", product.getPrice());
        System.out.println(
                "- " + product.getName() + " " + priceWithComma + "원 " + stockInfo + promotionInfo);
    }

    private void printProductWithRegularStock(Product product) {
        String stockInfo = product.getRegularStock() + "개";
        String priceWithComma = String.format("%,d", product.getPrice());
        System.out.println(
                "- " + product.getName() + " " + priceWithComma + "원 " + stockInfo);
    }

    private void printNoPromotionStockWithPromotion(Product product) {
        String priceWithComma = String.format("%,d", product.getPrice());
        String promotionInfo = getPromotionInfo(product);
        System.out.println(
                "- " + product.getName() + " " + priceWithComma + "원 재고 없음" + promotionInfo);
    }

    private void printNoRegularStockWithPromotion(Product product) {
        String priceWithComma = String.format("%,d", product.getPrice());
        String promotionInfo = getPromotionInfo(product);
        System.out.println(
                "- " + product.getName() + " " + priceWithComma + "원 재고 없음");
    }

    private String getPromotionInfo(Product product) {
        return product.getPromotion().map(promotion -> " " + promotion.getName()).orElse("");
    }

    public void printReceipt(Receipt receipt) {
        System.out.println("==============W 편의점================");
        printOrderItems(receipt.getOrderItems());
        System.out.println("=============증   정===============");
        printGiftItems(receipt.getGiftItems());
        System.out.println("====================================");
        printTotals(receipt);
    }

    private void printOrderItems(List<OrderItem> items) {
        System.out.println("상품명\t\t수량\t금액");
        for (OrderItem item : items) {
            System.out.printf("%-10s\t%5d\t%,10d%n", item.getProductName(), item.getQuantity(), item.getAmount());
        }
    }

    private void printGiftItems(List<GiftItem> gifts) {
        for (GiftItem gift : gifts) {
            System.out.printf("%-10s\t%5d%n", gift.getProductName(), gift.getQuantity());
        }
    }

    private void printTotals(Receipt receipt) {
        System.out.printf("총구매액\t\t%5d\t%,10d%n", receipt.getTotalQuantity(), receipt.getTotalAmount());
        if (receipt.getPromotionDiscount() > 0) {
            System.out.printf("행사할인\t\t\t-%,10d%n", receipt.getPromotionDiscount());
        } else {
            System.out.printf("행사할인\t\t\t%10d%n", 0);
        }
        if (receipt.getMembershipDiscount() > 0) {
            System.out.printf("멤버십할인\t\t\t-%,10d%n", receipt.getMembershipDiscount());
        } else {
            System.out.printf("멤버십할인\t\t\t%10d%n", 0);
        }
        System.out.printf("내실돈\t\t\t%,10d%n", receipt.getFinalAmount());
    }
}
