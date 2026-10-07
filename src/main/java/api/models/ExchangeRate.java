package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRate {
    private String iso1;
    private String iso2;
    private double buyRate;
    private double sellRate;
    private int conversionType;
    private double currencyWeight;
    private String updateDate;
}
