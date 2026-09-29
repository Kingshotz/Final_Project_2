package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CurrencyRate{
    private final String code;
    private final double buyRate;
    private final double sellRate;
    private final int baseAmount;
}
