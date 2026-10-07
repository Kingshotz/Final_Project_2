package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MortgageLoan {
    private final LoanCurrency currency;
    private final long amount;
    private final int years;
    private final double interestRate;
    private final double effectiveRate;
}
