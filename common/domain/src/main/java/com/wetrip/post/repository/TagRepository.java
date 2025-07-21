package com.wetrip.post.repository;

import com.wetrip.post.entity.Tags;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tags, Long> {

  List<Tags> findByPostId(Long postId);
}
