package post.repository;


import post.entity.JoinComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinCommentRepository extends JpaRepository<JoinComment, Long> {
}
