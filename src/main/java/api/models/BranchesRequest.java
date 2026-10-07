package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchesRequest {
    private List<String> filter;
    private String locale;
    private String keyword;
    private GeoPoint myLocation;
}
