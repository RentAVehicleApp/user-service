package rent.vehicle.useerservice.app.service.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import rent.vehicle.dto.request.SearchCriteria;
import rent.vehicle.dto.request.SearchUserRequest;
import rent.vehicle.useerservice.app.domain.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class UserSpecificationBuilder {
    public Specification<UserEntity> buildFromRequest(SearchUserRequest searchUserRequest) {
        if(searchUserRequest.getSearchCriteria()==null || searchUserRequest.getSearchCriteria().isEmpty()){
            return null;
        }

        List<SearchCriteria> searchCriteria = searchUserRequest.getSearchCriteria();

        List<Specification<UserEntity>> specifications = searchCriteria.stream()
                .map(this::buildFromCriteria)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return specifications.stream()
                .reduce(Specification::and)
                .orElse(null);
    }
    private Specification<UserEntity> buildFromCriteria(SearchCriteria searchCriteria) {
        return(root,query,cb)-> {
            return switch (searchCriteria.getOperation()) {
                case EQUALS -> cb.equal(root.get(searchCriteria.getFilter()), searchCriteria.getValue());
                case NOT_EQUALS -> cb.notEqual(root.get(searchCriteria.getFilter()), searchCriteria.getValue());
                case CONTAINS -> {
                    String likePattern = '%' + searchCriteria.getValue().toString().toLowerCase() + '%';
                    yield cb.like(
                            cb.lower(root.get(searchCriteria.getFilter())), likePattern
                    );
                }
                case STARTS_WITH ->
                        cb.like(root.get(searchCriteria.getFilter()), searchCriteria.getValue().toString().toLowerCase() + '%');
                case END_WITH ->
                        cb.like(root.get(searchCriteria.getFilter()), "%" + searchCriteria.getValue().toString().toLowerCase());
                case GREATER_THAN -> cb.greaterThan(root.get(searchCriteria.getFilter()),
                        (Comparable) searchCriteria.getValue());
                case LESS_THAN -> cb.lessThan(root.get(searchCriteria.getFilter()),
                        (Comparable) searchCriteria.getValue());
                default -> null;
            };
        };
    }
}
