package rent.vehicle.useerservice.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rent.vehicle.enums.UserStatus;
import rent.vehicle.useerservice.app.domain.UserEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,String>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByPhoneNumber(String phoneNumber);

    boolean existsUserEntityByEmailAndIdNot(String email, Long id);

    @Query("SELECT u FROM UserEntity u WHERE u.status = 'ACTIVE'" +
            "AND u.email LIKE %:search% OR u.phoneNumber LIKE %:search%")
    Page<UserEntity> findActiveUsers(@Param("search") String search, Pageable pageable);


    boolean existsUserEntityByEmail(String email);

    boolean existsUserEntityByPhoneNumber(String phoneNumber);

    boolean existsUserEntityByPhoneNumberAndIdNot(String phone, Long excludeUserId);

    Optional<Object> findById(Long userId);

    UserEntity findUserEntityById(Long id);

    List<UserEntity> findUserEntitiesByStatusContainsAndUpdatedAtBefore(UserStatus status, Instant updatedAtBefore);
}
