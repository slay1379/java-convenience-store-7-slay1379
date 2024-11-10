package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import store.domain.Product;
import store.domain.Promotion;

public class ProductLoader {
    private static final String PRODUCTS_FILE_PATH = "src/main/resources/products.md";

    public List<Product> readProducts(List<Promotion> promotions) {
        return loadProducts(promotions,PRODUCTS_FILE_PATH);
    }
    public List<Product> loadProducts(List<Promotion> promotions, String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // 헤더 스킵
            List<String> productNames = new ArrayList<>(); // 추가된 부분
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                String name = splitLine[0].trim();
                int price = Integer.parseInt(splitLine[1].trim());
                int stock = Integer.parseInt(splitLine[2].trim());
                String promotionName = splitLine[3].trim().equals("null") ? null : splitLine[3].trim();
                Promotion promotion = null;
                if (promotionName != null) {
                    promotion = findPromotionByName(promotions, promotionName);
                }
                Product product = new Product(name, price, stock, promotion);
                products.add(product);
                productNames.add(name); // 추가된 부분
            }
            // 일반 상품이 없는 경우 재고 0인 일반 상품 추가
            addMissingRegularProducts(products, productNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    private void addMissingRegularProducts(List<Product> products, List<String> productNames) {
        List<String> uniqueNames = new ArrayList<>();
        for (Product product : products) {
            if (!uniqueNames.contains(product.getName())) {
                uniqueNames.add(product.getName());
            }
        }
        for (String name : uniqueNames) {
            boolean hasRegular = false;
            boolean hasPromotion = false;
            int price = 0;
            for (Product product : products) {
                if (product.getName().equals(name)) {
                    price = product.getPrice();
                    if (!product.getPromotion().isPresent()) {
                        hasRegular = true;
                    } else {
                        hasPromotion = true;
                    }
                }
            }
            if (!hasRegular && hasPromotion) {
                // 재고 0인 일반 상품 추가
                products.add(new Product(name, price, 0, null));
            }
        }
    }

    private Promotion findPromotionByName(List<Promotion> promotions, String name) {
        for (Promotion promotion : promotions) {
            if (promotion.getName().equals(name)) {
                return promotion;
            }
        }
        return null;
    }
}
