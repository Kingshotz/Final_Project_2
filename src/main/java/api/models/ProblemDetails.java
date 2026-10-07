package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemDetails {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String code;
    private Map<String, List<String>> errors;
}
