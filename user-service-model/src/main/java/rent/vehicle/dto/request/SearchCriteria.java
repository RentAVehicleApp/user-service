package rent.vehicle.dto.request;

import lombok.Getter;
import lombok.Setter;
import rent.vehicle.enums.Operations;

@Getter
@Setter
public class SearchCriteria {
   String filter;
   Operations operation;
   String value;
}
