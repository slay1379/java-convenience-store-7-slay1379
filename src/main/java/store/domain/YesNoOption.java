package store.domain;

public enum YesNoOption {
    YES("Y"),
    NO("N");

    private final String answer;

    YesNoOption(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
