package rent.vehicle.useerservice.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rent.vehicle.common.CustomPage;
import rent.vehicle.enums.CustomerStatus;
import rent.vehicle.useerservice.app.domain.CustomerEntity;

import java.time.Instant;
import java.util.List;

public interface CustomerRepository extends JpaRepository<CustomerEntity,Long>, JpaSpecificationExecutor<CustomerEntity> {
    CustomerEntity findUserEntityByEmail(String email);

//    CustomerEntity findUserEntityByPhoneNumber(String phoneNumber);
//
//    @Query("SELECT u FROM CustomerEntity u WHERE u.status = :status " +
//            "AND (u.email LIKE %:search% OR u.phoneNumber LIKE %:search%)")
//    CustomPage<CustomerEntity> findActiveUsers(@Param("search") String search, @Param("status") CustomerStatus customerStatus, Pageable pageable);


    boolean existsUserEntityByEmail(String email);
    boolean existsUserEntityByEmailAndIdNot(String email, Long id);
    boolean existsUserEntityByPhoneNumber(String phoneNumber);
    boolean existsUserEntityByPhoneNumberAndIdNot(String phoneNumber, Long id);


    List<CustomerEntity> findUserEntitiesByStatusAndUpdatedAtBefore(CustomerStatus status, Instant updatedAt);
}
