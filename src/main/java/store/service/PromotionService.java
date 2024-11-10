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
        // 프로모션이 없으면 적용 불가
        if (!product.getPromotion().isPresent()) {
            return false;
        }

        Promotion promotion = product.getPromotion().get();
        // 프로모션이 현재 유효한지 확인
        return isPromotionActive(promotion);
    }

    private boolean isPromotionActive(Promotion promotion) {
        LocalDate now = LocalDate.now();
        return promotion.isPromotionActive(now);
    }

    public int calculateGetQuantity(Product product, int quantity) {
        // 프로모션 적용 가능한지 확인
        if (!isPromotionApplicable(product)) {
            return 0;
        }

        Promotion promotion = product.getPromotion().get();

        // 예: 2+1 프로모션에서 buy=2, get=1
        int buy = promotion.getBuy();  // 구매해야 하는 수량
        int get = promotion.getGet();  // 증정받는 수량

        // 프로모션 적용 횟수 계산
        // 예: 3개 구매시 buy=2면 1번 적용, 4개 구매시 buy=2면 2번 적용
        int numPromotions = quantity / buy;

        // 증정 수량 계산
        int totalGetQuantity = numPromotions * get;

        // 재고 확인
        return Math.min(totalGetQuantity, product.getStock());
    }
}