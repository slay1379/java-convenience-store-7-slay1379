package store.domain;

public class Product {
    private final String name;
    private final int price;
    private int stock;
    private int promoStock;
    private final String promotion;

    public Product(String name, int price, int stock, int promoStock, String promotion) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.promoStock = promoStock;
        this.promotion = promotion;
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

    public int getPromoStock() {
        return this.promoStock;
    }

    public String getPromotion() {
        return this.promotion;
    }

    public void reduceStock(int quantity) {
        this.stock -= quantity;
    }

    public void reducePromoStock(int quantity) {
        this.promoStock -= quantity;
    }
}
