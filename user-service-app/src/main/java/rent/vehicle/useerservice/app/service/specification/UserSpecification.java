package rent.vehicle.useerservice.app.service.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import rent.vehicle.dto.request.SearchUserRequest;
import rent.vehicle.enums.UserStatus;
import rent.vehicle.useerservice.app.domain.UserEntity;


public class UserSpecification {
    public static Specification<UserEntity>isNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), UserStatus.DELETED));
    }

}
