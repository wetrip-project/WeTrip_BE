package post.repository;


import post.entity.JoinHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinHistoryRepository extends JpaRepository<JoinHistory, Long> {
}
