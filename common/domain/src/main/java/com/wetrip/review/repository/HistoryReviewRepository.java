package com.wetrip.review.repository;


import com.wetrip.review.entity.HistoryReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryReviewRepository extends JpaRepository<HistoryReview, Long> {
}
