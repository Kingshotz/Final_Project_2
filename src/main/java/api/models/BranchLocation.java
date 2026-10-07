package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchLocation {
    private long id;
    private String name;
    private String address;
    private String regionName;
    private String type;
    private double latitude;
    private double longitude;
    private List<String> currencies;
    private List<WorkHour> workHours;
    private boolean worksallday;
}
