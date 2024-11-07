package store.domain;

public enum MembershipDiscount {
    STANDARD(0.3, 8000);

    private final double discountRate;
    private final int discountLimit;

    MembershipDiscount(double discountRate, int discountLimit) {
        this.discountRate = discountRate;
        this.discountLimit = discountLimit;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public int getDiscountLimit() {
        return discountLimit;
    }
}
