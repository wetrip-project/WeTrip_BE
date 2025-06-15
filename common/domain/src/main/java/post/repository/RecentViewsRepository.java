package post.repository;


import post.entity.RecentViews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentViewsRepository extends JpaRepository<RecentViews, Long> {
}
