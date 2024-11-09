package store.domain;

import java.util.List;

public class Receipt {
    public List<OrderItem> orderItems;
    public List<GiftItem> giftItems;
    public int totalAmount;
    public int promotionDiscount;
    public int membershipDiscount;
    public int finalAmount;

    public Receipt(List<OrderItem> orderItems, List<GiftItem> giftItems, int totalAmount, int promotionDiscount,
                   int membershipDiscount, int finalAmount) {
        this.orderItems = orderItems;
        this.giftItems = giftItems;
        this.totalAmount = totalAmount;
        this.promotionDiscount = promotionDiscount;
        this.membershipDiscount = membershipDiscount;
        this.finalAmount = finalAmount;
    }

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
