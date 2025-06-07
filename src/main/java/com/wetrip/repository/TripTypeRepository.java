package com.wetrip.repository;


import com.wetrip.entity.TripType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripTypeRepository extends JpaRepository<TripType, Long> {
}
