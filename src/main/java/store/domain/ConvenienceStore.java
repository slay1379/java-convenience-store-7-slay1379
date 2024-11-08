package store.domain;

import java.util.List;

public class ConvenienceStore {
    private List<Product> products;
    private List<Promotion> promotions;

    public ConvenienceStore(List<Product> products, List<Promotion promotions) {
        this.products = products;
        this.promotions = promotions;
    }
}
