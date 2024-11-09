package store.view;

import java.util.List;
import store.domain.Product;

public class OutputView {

    private static final String GREETING_MESSAGE = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.";

    public void printProducts(List<Product> products) {
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
        return product.getPromotion().map(promotion -> promotion.getName()).orElse("");
    }
}
