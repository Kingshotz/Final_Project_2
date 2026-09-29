package database.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import models.ConsumerLoanValidation;

@Data
@NoArgsConstructor
public class ConsumerLoanCase {
    private int id;
    private String description;
    private long amount;
    private int months;
    private ConsumerLoanValidation expectedResult;

    @Override
    public String toString() {
        return "#" + id + " " + description + " (" + amount + " GEL, " + months + " months, " + expectedResult + ")";
    }
}
