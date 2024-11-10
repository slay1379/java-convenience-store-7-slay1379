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
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
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

    public String getIdentifier() {
        return name + (promotion.map(p -> "_" + p.getName()).orElse(""));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name).append(" ");

        result.append(String.format("%,d", price)).append("원 ");

        if (stock > 0) {
            result.append(stock).append("개");
        } else {
            result.append("재고 없음");
        }

        promotion.ifPresent(p -> result.append(" ").append(p.getName()));
        return result.toString();
    }
}
