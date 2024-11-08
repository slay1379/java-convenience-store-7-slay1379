package store.domain;

import java.util.List;

public class Receipt {
    public List<OrderItem> orderItems;
    public List<GiftItem> giftItems;
    public int totalAmount;
    public int promotionDiscount;
    public int membershipDiscount;
    public int finalAmount;

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public List<GiftItem> getGiftItems() {
        return giftItems;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }
}
