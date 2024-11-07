package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.domain.Product;
import store.exception.MessageConstants;

public class ProductLoader {
    public List<Product> readProducts() {
        String filePath = "src/main/resources/products.md";
        return loadProducts(filePath);
    }

    private static List<Product> loadProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            skipHeader(br);
            readLines(br, products);
        } catch (IOException e) {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_READ_EXCEPTION);
        }
        return products;
    }

    private static void skipHeader(BufferedReader br) throws IOException {
        br.readLine();
    }

    private static void readLines(BufferedReader br, List<Product> products) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            addProductIfValid(line, products);
        }
    }

    private static void addProductIfValid(String line, List<Product> products) {
        String[] fields = line.split(",");
        if (fields.length == 4) {
            products.add(createProduct(fields));
        } else {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_FORM_EXCEPTION);
        }
    }

    private static Product createProduct(String[] fields) {
        String name = fields[0].trim();
        int price = Integer.parseInt(fields[1].trim());
        int quantity = Integer.parseInt(fields[2].trim());
        String promotion = fields[3].trim();
        if ("null".equalsIgnoreCase(promotion)) {
            promotion = null;
        }
        return new Product(name, price, quantity, promotion);
    }
}
