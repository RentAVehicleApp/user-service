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

public interface UserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM UserEntity u WHERE u.status = :status " +
            "AND (u.email LIKE %:search% OR u.phoneNumber LIKE %:search%)")
    Page<UserEntity> findActiveUsers(@Param("search") String search, @Param("status")UserStatus userStatus, Pageable pageable);


    boolean existsUserEntityByEmail(String email);
    boolean existsUserEntityByEmailAndIdNot(String email, Long id);
    boolean existsUserEntityByPhoneNumber(String phoneNumber);
    boolean existsUserEntityByPhoneNumberAndIdNot(String phoneNumber, Long id);


    List<UserEntity> findUserEntitiesByStatusAndUpdatedAtBefore(UserStatus status, Instant updatedAt);
}
