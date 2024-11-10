package store.domain;

public class OrderItem {
    private String productIdentifier;
    private String productName;
    private int quantity;
    private int amount;

    public OrderItem(String productIdentifier, String productName, int quantity, int amount) {
        this.productIdentifier = productIdentifier;
        this.productName = productName;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getProductIdentifier() {
        return productIdentifier;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAmount() {
        return amount;
    }
}
