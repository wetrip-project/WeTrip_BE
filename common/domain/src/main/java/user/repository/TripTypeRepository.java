package user.repository;


import user.entity.TripType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripTypeRepository extends JpaRepository<TripType, Long> {
}
