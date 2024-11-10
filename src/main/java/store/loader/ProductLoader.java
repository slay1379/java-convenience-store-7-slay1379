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
            br.readLine();
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

                Product existingProduct = findProductByName(products, name);
                if (existingProduct != null) {
                    if (promotion == null) {
                        existingProduct.addRegularStock(stock);
                    } else {
                        existingProduct.addPromotionStock(stock, promotion);
                    }
                } else {
                    Product product = new Product(name, price);
                    if (promotion == null) {
                        product.addRegularStock(stock);
                    } else {
                        product.addPromotionStock(stock, promotion);
                    }
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    private Product findProductByName(List<Product> products, String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
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
