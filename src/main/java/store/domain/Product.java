package store.domain;

public class Product {
    private final String name;
    private final int price;
    private int stock;
    private int promoStock;
    private final String promotionName;

    public Product(String name, int price, int stock, int promoStock, String promotionName) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.promoStock = promoStock;
        this.promotionName = promotionName;
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

    public String getPromotionName() {
        return this.promotionName;
    }

    public void reduceStock(int quantity) {
        this.stock -= quantity;
    }

    public void reducePromoStock(int quantity) {
        this.promoStock -= quantity;
    }
}
