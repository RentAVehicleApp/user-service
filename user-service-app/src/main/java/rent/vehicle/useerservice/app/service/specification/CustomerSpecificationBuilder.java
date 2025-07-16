package rent.vehicle.useerservice.app.service.specification;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import rent.vehicle.dto.request.SearchCriteria;
import rent.vehicle.dto.request.SearchCustomerRequest;
import rent.vehicle.useerservice.app.domain.CustomerEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class CustomerSpecificationBuilder {
    public Specification<CustomerEntity> buildFromRequest(SearchCustomerRequest searchUserRequest) {
        if(searchUserRequest.getSearchCriteria()==null || searchUserRequest.getSearchCriteria().isEmpty()){
            return null;
        }

        List<SearchCriteria> searchCriteria = searchUserRequest.getSearchCriteria();

        List<Specification<CustomerEntity>> specifications = searchCriteria.stream()
                .map(this::buildFromCriteria)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return specifications.stream()
                .reduce(Specification::and)
                .orElse(null);
    }
    private Specification<CustomerEntity> buildFromCriteria(SearchCriteria criteria) {
        return (root, query, cb) -> {
            String field = criteria.getFilter();
            Path<?> path = root.get(field);
            Class<?> javaType = path.getJavaType();

            // конвертация строки в нужный тип
            Object value;
            String stringValue = criteria.getValue();
            if (javaType.isEnum()) {
                @SuppressWarnings("unchecked")
                Class<? extends Enum> enumType = (Class<? extends Enum>) javaType;
                value = Enum.valueOf(enumType, stringValue);
            } else {
                value = stringValue;
            }

            switch (criteria.getOperation()) {
                case EQUALS:
                    return cb.equal(path, value);

                case NOT_EQUALS:
                    return cb.notEqual(path, value);

                case CONTAINS:
                    return cb.like(
                            cb.lower(root.get(field).as(String.class)),
                            "%" + stringValue.toLowerCase() + "%"
                    );

                case STARTS_WITH:
                    return cb.like(
                            cb.lower(root.get(field).as(String.class)),
                            stringValue.toLowerCase() + "%"
                    );

                case END_WITH:
                    return cb.like(
                            cb.lower(root.get(field).as(String.class)),
                            "%" + stringValue.toLowerCase()
                    );

                case GREATER_THAN:
                    @SuppressWarnings("unchecked")
                    Comparable<Object> compGt = (Comparable<Object>) value;
                    return cb.greaterThan(root.get(field).as(compGt.getClass()), compGt);

                case LESS_THAN:
                    @SuppressWarnings("unchecked")
                    Comparable<Object> compLt = (Comparable<Object>) value;
                    return cb.lessThan(root.get(field).as(compLt.getClass()), compLt);

                default:
                    return null;
            }
        };
    }

}

