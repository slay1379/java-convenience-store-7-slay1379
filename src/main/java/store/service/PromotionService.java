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
        return product.getPromotion().isPresent() && isPromotionActive(product.getPromotion().get());
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
        return calculateEligibleGetQuantity(promotion, quantity);
    }

    private int calculateEligibleGetQuantity(Promotion promotion, int quantity) {
        return (quantity >= promotion.getBuy())
                ? (quantity / (promotion.getBuy() + promotion.getGet())) * promotion.getGet()
                : 0;
    }

    public int calculateAdditionalFreeQuantity(Product product, int currentQuantity) {
        if (!product.getPromotion().isPresent()) {
            return 0;
        }
        Promotion promotion = product.getPromotion().get();
        int finalCurrentQuantity = currentQuantity % (promotion.getBuy() + promotion.getGet());
        return (promotion.getBuy() == finalCurrentQuantity) ? promotion.getGet() : 0;
    }
}
