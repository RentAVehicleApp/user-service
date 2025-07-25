package rent.vehicle.useerservice.app.service.specification;

import org.springframework.data.jpa.domain.Specification;
import rent.vehicle.enums.CustomerStatus;
import rent.vehicle.useerservice.app.domain.CustomerEntity;


public class CustomerSpecification {
    public static Specification<CustomerEntity>isNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), CustomerStatus.DELETED));
    }
    public static Specification<CustomerEntity> nameContains(String pattern){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + pattern.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),  "%" + pattern.toLowerCase() + "%")
                )
        );
    }
    public static Specification<CustomerEntity>emailEndWith(String pattern){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + "@" + pattern.toLowerCase()));
    }
    }

