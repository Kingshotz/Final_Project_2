package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConsumerLoanValidation {
    VALID(false, false),
    AMOUNT_TOO_LOW(true, false),
    AMOUNT_TOO_HIGH(true, false),
    PERIOD_TOO_SHORT(false, true),
    PERIOD_TOO_LONG(false, true);

    private final boolean amountInvalid;
    private final boolean periodInvalid;
}
