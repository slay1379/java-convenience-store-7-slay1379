package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputView {

    private static final String INPUT_PRODUCT_NAME_QUANTITY_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String INPUT_MEMBERSHIP_DISCOUNT = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private static final String INPUT_PURCHASE_OTHER_PRODUCT = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
    private static final String PRODUCT_QUANTITY_PATTERN = "^\\[([가-힣a-zA-Z]+)-(\\d+)\\]$";

    public String readProduct() {
        System.out.println(INPUT_PRODUCT_NAME_QUANTITY_MESSAGE);
        return Console.readLine();
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
            isValidProduct(product);
        }
    }

    private boolean isValidProduct(String product) {
        isValidName(String name);
        isValidQuantity(String quantity);
    }

    private boolean isValidProductPattern(String input) {
        Pattern pattern = Pattern.compile(PRODUCT_QUANTITY_PATTERN);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
