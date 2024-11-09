package store.domain;

import java.time.LocalDate;

public class Promotion {
    private final String name;
    private final int buy;
    private final int get;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(String name, int buy, int get, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return this.name;
    }

    public int getBuy() {
        return this.buy;
    }

    public int getGet() {
        return this.get;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public boolean isPromotionActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
