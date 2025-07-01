package rent.vehicle.useerservice.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import rent.vehicle.useerservice.app.domain.AdressEntity;

import java.util.List;

@Repository
public interface AdressRepository extends JpaRepository<AdressEntity, Long>, JpaSpecificationExecutor<AdressEntity> {

}
