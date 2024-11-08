package store.controller;

import java.util.List;
import store.domain.ConvenienceStore;
import store.domain.Product;
import store.domain.Promotion;
import store.loader.ProductLoader;
import store.loader.PromotionLoader;

public class ConvenienceStoreController {
    private ConvenienceStore convenienceStore;
    private List<Product> products;
    private List<Promotion> promotions;

    public ConvenienceStoreController(List<Product> products, List<Promotion> promotions) {
        this.products = products;
        this.promotions = promotions;
        this.convenienceStore = new ConvenienceStore(products, promotions);
    }
}
