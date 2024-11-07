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
            System.out.println(INPUT_PRODUCT_NAME_QUANTITY_MESSAGE);
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
        System.out.println(INPUT_MEMBERSHIP_DISCOUNT);
        return Console.readLine();
    }

    public String readPurchaseOtherProduct() {
        System.out.println(INPUT_PURCHASE_OTHER_PRODUCT);
        return Console.readLine();
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
        return content.split("-");
    }

    private void validateProductPattern(String input) {
        Pattern pattern = Pattern.compile(PRODUCT_QUANTITY_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.PATTERN_EXCEPTION);
        }
    }

    private void validateProductName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(MessageConstants.ERROR + MessageConstants.PRODUCT_NAME_EMPTY);
        }
    }

    private void validateProductQuantity(String quantity) {
        try
    }
}
