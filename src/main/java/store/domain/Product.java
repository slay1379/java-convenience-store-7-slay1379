package store.domain;

public class Product {

    private final String name;
    private final int price;
    private final int quantity;
    private final String promotion;

    public Product(String name, int price, int quantity, String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public String getName() {
        return this.name;
    }
}
