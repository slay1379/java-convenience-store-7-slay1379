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
        System.out.println("=========== 영수증 ===========");
        printOrderItems(receipt.getOrderItems());
        printGiftItems(receipt.getGiftItems());
        printTotals(receipt);
    }

    private void printOrderItems(List<OrderItem> items) {
        System.out.println("상품명\t수량\t금액");
        for (OrderItem item : items) {
            String amountWithComma = String.format("%,d", item.getAmount());
            System.out.println(item.getProductName() + "\t" + item.getQuantity() + "\t" + amountWithComma);
        }
    }

    private void printGiftItems(List<GiftItem> gifts) {
        if (gifts.isEmpty()) {
            return;
        }
        System.out.println("\n증정상품\t수량");
        for (GiftItem gift : gifts) {
            System.out.println(gift.getProductName() + "\t" + gift.getQuantity());
        }
    }

    private void printTotals(Receipt receipt) {
        System.out.println("----------------------------");
        System.out.println("총 구매액:\t" + receipt.getTotalAmount() + "원");
        if (receipt.getPromotionDiscount() > 0) {
            System.out.println("프로모션 할인:\t-%" + receipt.getPromotionDiscount() + "원");
        }
        if (receipt.getMembershipDiscount() > 0) {
            System.out.println("멤버십 할인:\t-" + receipt.getMembershipDiscount() + "원");
        }
        System.out.println("----------------------------");
        System.out.println("내실돈:\t\t" + receipt.getFinalAmount() + "원");
    }
}
