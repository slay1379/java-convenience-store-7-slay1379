package store.view;

import camp.nextstep.edu.missionutils.Console;

public class InputView {

    private static final String INPUT_PRODUCT_NAME_QUANTITY_MESSAGE = "구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String INPUT_MEMBERSHIP_DISCOUNT = "멤버십 할인을 받으시겠습니까? (Y/N)";

    public String readProductNameAndQuantity() {
        System.out.println(INPUT_PRODUCT_NAME_QUANTITY_MESSAGE);
        return Console.readLine();
    }

    public String readMembershipDiscount() {
        System.out.println(INPUT_MEMBERSHIP_DISCOUNT);
        return Console.readLine();
    }
}
