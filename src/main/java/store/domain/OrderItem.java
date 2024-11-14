package store.domain;

public class OrderItem {
    private String productName;
    private int quantity;
    private int amount;
    private int usedPromotionStock;

    public OrderItem(String productName, int quantity, int amount, int usedPromotionStock) {
        this.productName = productName;
        this.quantity = quantity;
        this.amount = amount;
        this.usedPromotionStock = usedPromotionStock;
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

    public int getUsedPromotionStock() {
        return usedPromotionStock;
    }
}
