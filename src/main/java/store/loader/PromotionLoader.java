package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.print.DocFlavor.STRING;
import store.domain.Promotion;
import store.exception.MessageConstants;

public class PromotionLoader {
    private static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";

    public List<Promotion> readPromotion() {
        return loadPromotions(PROMOTION_FILE_PATH);
    }

    private List<Promotion> loadPromotions(String filePath) {
        List<Promotion> promotions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            skipHeader(br);
            readLines(br, promotions);
        } catch (Exception e) {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_READ_EXCEPTION);
        }
        return promotions;
    }

    private void skipHeader(BufferedReader br) throws IOException {
        br.readLine();
    }

    private void readLines(BufferedReader br, List<Promotion> promotions) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            addPromotionIfValid(line, promotions);
        }
    }

    private void addPromotionIfValid(String line, List<Promotion> promotions) {
        String[] fields = line.split(",");
        if (fields.length != 5) {
            System.out.println(MessageConstants.ERROR + MessageConstants.FILE_FORM_EXCEPTION);
            return;
        }
        Promotion promotion = createPromotion(fields);
        promotions.add(promotion);
    }

    private Promotion createPromotion(String[] fields) {
        String name = fields[0].trim();
        int buy = Integer.parseInt(fields[1].trim());
        int get = Integer.parseInt(fields[2].trim());
        LocalDate startDate = LocalDate.parse(fields[3].trim());
        LocalDate endDate = LocalDate.parse(fields[4].trim());
        return new Promotion(name, buy, get, startDate, endDate);
    }
}
