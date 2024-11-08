package store.domain;

public class GiftItem {
    private String productName;
    private int quantity;

    public GiftItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
