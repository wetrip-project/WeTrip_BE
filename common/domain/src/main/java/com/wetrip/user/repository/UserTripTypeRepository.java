package com.wetrip.user.repository;


import com.wetrip.user.entity.UserTripType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTripTypeRepository extends JpaRepository<UserTripType, Long> {

    List<UserTripType> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
