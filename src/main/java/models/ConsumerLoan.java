package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ConsumerLoan {
    private final long amount;
    private final int months;
    private final double interestRate;
    private final double effectiveRate;
}
