package com.wetrip.repository;


import com.wetrip.entity.UserTripType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTripTypeRepository extends JpaRepository<UserTripType, Long> {
}
