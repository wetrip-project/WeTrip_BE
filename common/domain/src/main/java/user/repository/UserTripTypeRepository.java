package user.repository;


import user.entity.UserTripType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTripTypeRepository extends JpaRepository<UserTripType, Long> {
}
