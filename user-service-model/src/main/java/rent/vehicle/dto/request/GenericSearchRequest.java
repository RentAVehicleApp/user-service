package rent.vehicle.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GenericSearchRequest {
    private List<SearchCriteria> searchCriteriaList;
    private int page = 0;
    private int size = 20;
    private String sort = "id,desc";
}
