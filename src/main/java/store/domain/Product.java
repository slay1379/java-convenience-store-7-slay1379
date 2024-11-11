package store.domain;

import java.util.Optional;
import store.exception.MessageConstants;

public class Product {
    private final String name;
    private final int price;
    private int regularStock;
    private int promotionStock;
    private Promotion promotion;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
        this.regularStock = 0;
        this.promotionStock = 0;
        this.promotion = null;
    }

    // Getter 메소드들
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRegularStock() {
        return regularStock;
    }

    public int getPromotionStock() {
        return promotionStock;
    }

    public Optional<Promotion> getPromotion() {
        return Optional.ofNullable(promotion);
    }

    public void addRegularStock(int stock) {
        this.regularStock += stock;
    }

    public void addPromotionStock(int stock, Promotion promotion) {
        this.promotionStock += stock;
        this.promotion = promotion;
    }

    public void reduceStock(int quantity, boolean isPromotion) {
        if (isPromotion && promotionStock >= quantity) {
            promotionStock -= quantity;
            return;
        }
        if (!isPromotion && regularStock >= quantity) {
            regularStock -= quantity;
        }
    }
}
