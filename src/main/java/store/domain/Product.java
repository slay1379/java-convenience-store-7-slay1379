package store.domain;

import java.util.Optional;
import store.exception.MessageConstants;

public class Product {
    private final String name;
    private final int price;
    private int stock;
    private Optional<Promotion> promotion;

    public Product(String name, int price, int stock, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.promotion = Optional.ofNullable(promotion);
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public int getStock() {
        return this.stock;
    }

    public Optional<Promotion> getPromotion() {
        return promotion;
    }

    public void reduceStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.QUANTITY_OVER_STOCK_EXCEPTION);
        }
        stock -= quantity;
    }
}
