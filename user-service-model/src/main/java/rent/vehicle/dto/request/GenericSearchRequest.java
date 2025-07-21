package rent.vehicle.workerservicemodel.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GenericSearchRequest {
    private List<SearchCriteria> searchCriteriaList;
    private int page = 0;
    private int size = 20;
    private String sort = "id,desc";
}
