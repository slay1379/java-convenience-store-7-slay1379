package store;

import java.util.List;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import store.controller.StoreController;
import store.domain.Product;
import store.domain.Promotion;
import store.loader.ProductLoader;
import store.loader.PromotionLoader;

public class Application {
    public static void main(String[] args) {
        PromotionLoader promotionLoader = new PromotionLoader();
        List<Promotion> promotions = promotionLoader.readPromotion();

        ProductLoader productLoader = new ProductLoader();
        List<Product> products = productLoader.readProducts(promotions);

        StoreController storeController = new StoreController(products, promotions);
        storeController.run();
    }
}
