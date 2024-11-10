package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.exception.MessageConstants;

public class InputView {

    private static final String INPUT_PRODUCT_NAME_QUANTITY_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String INPUT_MEMBERSHIP_DISCOUNT = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private static final String INPUT_PURCHASE_OTHER_PRODUCT = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
    private static final String PRODUCT_QUANTITY_PATTERN = "^\\[([가-힣a-zA-Z]+)-(\\d+)\\]$";

    public String readProduct() {
        while (true) {
            System.out.println("\n"+INPUT_PRODUCT_NAME_QUANTITY_MESSAGE);
            String input = Console.readLine();
            try {
                parseInputProduct(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(MessageConstants.ERROR + e.getMessage());
            }
        }
    }

    public String readMembershipDiscount() {
        System.out.println("\n"+INPUT_MEMBERSHIP_DISCOUNT);
        return readYOrN();
    }

    public String readPurchaseOtherProduct() {
        System.out.println("\n"+INPUT_PURCHASE_OTHER_PRODUCT);
        return readYOrN();
    }

    private String readYOrN() {
        while (true) {
            String input = Console.readLine();
            try {
                validateYOrN(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void parseInputProduct(String input) {
        String[] products = input.split(",");
        for (String product : products) {
            validateProduct(product);
        }
    }

    private void validateProduct(String product) {
        validateProductPattern(product);
        String[] productDetails = extractProductDetails(product);
        validateProductName(productDetails[0]);
        validateProductQuantity(productDetails[1]);
    }

    private String[] extractProductDetails(String product) {
        String content = product.substring(1, product.length() - 1);
        String[] details = content.split("-");
        if (details.length != 2 || details[0].isEmpty() || details[1].isEmpty()) {
            throw new IllegalArgumentException(MessageConstants.PATTERN_EXCEPTION);
        }
        return details;
    }

    private void validateProductPattern(String input) {
        Pattern pattern = Pattern.compile(PRODUCT_QUANTITY_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(MessageConstants.PATTERN_EXCEPTION);
        }
    }

    private void validateProductName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(MessageConstants.INPUT_PRODUCT_NAME_EMPTY_EXCEPTION);
        }
    }

    private void validateProductQuantity(String quantityStr) {
        int quantity = parseQuantity(quantityStr);
        if (quantity <= 0) {
            throw new IllegalArgumentException(MessageConstants.INPUT_PRODUCT_QUANTITY_BELOW_ZERO_EXCEPTION);
        }
    }

    private int parseQuantity(String quantityStr) {
        try {
            return Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageConstants.INPUT_PRODUCT_QUANTITY_NOT_NUMBER_EXCEPTION);
        }
    }

    private void validateYOrN(String input) {
        if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
            System.out.println("오류");
            throw new IllegalArgumentException(MessageConstants.PATTERN_EXCEPTION);
        }
    }
}
