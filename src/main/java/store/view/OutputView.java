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

        // 이름으로 상품들을 그룹화 (순서 유지)
        Map<String, List<Product>> productsByName = products.stream()
                .collect(Collectors.groupingBy(Product::getName, LinkedHashMap::new, Collectors.toList()));

        // 각 상품명에 대해 처리
        for (Map.Entry<String, List<Product>> entry : productsByName.entrySet()) {
            String productName = entry.getKey();
            List<Product> productList = entry.getValue();

            // 프로모션 상품과 일반 상품 분리
            Product promoProduct = null;
            Product regularProduct = null;

            for (Product product : productList) {
                if (product.getPromotion().isPresent()) {
                    promoProduct = product;
                } else {
                    regularProduct = product;
                }
            }

            // 프로모션 상품 출력
            if (promoProduct != null) {
                printProduct(promoProduct);
            }

            // 일반 상품 출력 또는 재고 없음 출력
            if (regularProduct != null) {
                if (regularProduct.getStock() > 0) {
                    printProduct(regularProduct);
                } else {
                    String priceWithComma = String.format("%,d", regularProduct.getPrice());
                    System.out.println("- " + regularProduct.getName() + " "
                            + priceWithComma + "원 재고 없음");
                }
            } else {
                // 일반 상품이 없는 경우 재고 없음 출력
                if (promoProduct != null) {
                    String priceWithComma = String.format("%,d", promoProduct.getPrice());
                    System.out.println("- " + promoProduct.getName() + " "
                            + priceWithComma + "원 재고 없음");
                }
            }
        }
    }

    private void printProduct(Product product) {
        String stockInfo = getStockInfo(product.getStock());
        String promotionInfo = getPromotionInfo(product);
        String priceWithComma = String.format("%,d", product.getPrice());
        System.out.println(
                "- " + product.getName() + " " + priceWithComma + "원 " + stockInfo + promotionInfo);
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
