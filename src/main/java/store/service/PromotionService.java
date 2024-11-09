package store.service;

import java.time.LocalDate;
import store.domain.Product;
import store.domain.Promotion;

public class PromotionService {
    public boolean isPromotionApplicable(Product product) {
        return product.getPromotion().filter(this::isPromotionActive).isPresent();
    }

    private boolean isPromotionActive(Promotion promotion) {
        LocalDate now = LocalDate.now();
        return promotion.isPromotionActive(now);
    }

    public int calculateGetQuantity(int quantity, Promotion promotion) {
        int groupSize = promotion.getBuy() + promotion.getGet();
        int numGroups = quantity / groupSize;
        return numGroups * promotion.getGet();
    }

    public int calculateTotalPrice(Product product, int quantity) {
        int unitPrice = product.getPrice();
        int total = unitPrice * quantity;
        Promotion promotion = product.getPromotion().orElse(null);
        if (promotion == null) {
            return total;
        }
        int getQuantity = calculateGetQuantity(quantity, promotion);
        return total - (getQuantity * unitPrice);
    }
}
