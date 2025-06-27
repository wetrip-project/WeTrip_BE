package com.wetrip.post.repository;


import com.wetrip.post.entity.RecentViews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentViewsRepository extends JpaRepository<RecentViews, Long> {
}
