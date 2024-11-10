package store.service;

import java.time.LocalDate;
import java.util.List;
import store.domain.Product;
import store.domain.Promotion;

public class PromotionService {
    private List<Promotion> promotions;

    public PromotionService(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public boolean isPromotionApplicable(Product product) {
        if (!product.getPromotion().isPresent()) {
            return false;
        }
        Promotion promotion = product.getPromotion().get();
        return isPromotionActive(promotion);
    }

    private boolean isPromotionActive(Promotion promotion) {
        LocalDate now = LocalDate.now();
        return promotion.isPromotionActive(now);
    }

    public int calculateGetQuantity(Product product, int quantity) {
        if (!isPromotionApplicable(product)) {
            return 0;
        }
        Promotion promotion = product.getPromotion().get();
        if (quantity >= promotion.getBuy()) {
            return (quantity / (promotion.getBuy() + promotion.getGet())) * promotion.getGet();
        }
        return 0;
    }
}
