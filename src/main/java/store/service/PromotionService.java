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

    public int calculateAdditionalFreeQuantity(Product product, int currentPromotionQuantity) {
        if (!product.getPromotion().isPresent()) {
            return 0;
        }

        return product.getPromotion()
                .map(promotion -> {
                    int buyQuantity = promotion.getBuy();
                    int getQuantity = promotion.getGet();

                    // 현재 프로모션 수량으로 받을 수 있는 무료 상품 수량 계산
                    int currentFreeItems = (currentPromotionQuantity / buyQuantity) * getQuantity;
                    int maxPossibleFreeItems = ((currentPromotionQuantity + getQuantity) / buyQuantity) * getQuantity;

                    // 추가로 받을 수 있는 수량 계산
                    int additionalPossible = maxPossibleFreeItems - currentFreeItems;

                    // 프로모션 재고와 비교하여 실제 가능한 수량 반환
                    return Math.min(additionalPossible, product.getPromotionStock());
                })
                .orElse(0);
    }
}
