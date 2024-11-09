package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;
import store.exception.MessageConstants;

public class ProductLoader {
    private static final String PRODUCT_FILE_PATH = "src/main/resources/products.md";
    private final Map<String, Promotion> promotionMap;

    public ProductLoader(List<Promotion> promotions) {
        this.promotionMap = new HashMap<>();
        for (Promotion promotion : promotions) {
            promotionMap.put(promotion.getName(), promotion);
        }
    }

    public List<Product> readProducts() {
        return loadProducts(PRODUCT_FILE_PATH);
    }

    private List<Product> loadProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            skipHeader(br);
            readLines(br, products);
        } catch (IOException e) {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_READ_EXCEPTION);
        }
        return products;
    }

    private void skipHeader(BufferedReader br) throws IOException {
        br.readLine();
    }

    private void readLines(BufferedReader br, List<Product> products) throws IOException {
        String line;
        Map<String, Product> productMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
            addOrUpdateProduct(line, productMap);
        }
        products.addAll(productMap.values());
    }

    private void addOrUpdateProduct(String line, Map<String, Product> productMap) {
        String[] fields = line.split(",");
        if (fields.length != 4) {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_FORM_EXCEPTION);
            return;
        }
        Product product = createProduct(fields);
        if (product == null) {
            return;
        }
        String productName = product.getName();
        if (productMap.containsKey(productName)) {
            mergeProduct(productMap.get(productName), product);
        } else {
            productMap.put(productName, product);
        }
    }

    private static Product createProduct(String[] fields) {
        try {
            String name = fields[0].trim();
            int price = Integer.parseInt(fields[1].trim());
            int stock = Integer.parseInt(fields[2].trim());
            String promotionName = getPromotionName(fields[3].trim());
            Promotion promotion = promotionMap.get(promotionName);
            return new Product(name, price, stock, promotion);
        } catch (NumberFormatException e) {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_FORM_EXCEPTION);
            return null;
        }
    }

    private String getPromotionName(String promotionField) {
        if ("null".equalsIgnoreCase(promotionField)) {
            return null;
        }
        return promotionField;
    }

    private void mergeProduct(Product existingProduct, Product newProduct) {
        existingProduct.reduceStock(-newProduct.getStock());
    }
}
