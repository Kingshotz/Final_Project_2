package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommercialRate {
    private String iso;
    private String name;
    private double buyRate;
    private double sellRate;
    private double officialCourse;
    private double weight;
}
