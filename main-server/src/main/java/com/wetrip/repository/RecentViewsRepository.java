package com.wetrip.repository;


import com.wetrip.entity.RecentViews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentViewsRepository extends JpaRepository<RecentViews, Long> {
}
