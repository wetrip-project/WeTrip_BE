package com.wetrip.user.repository;


import org.springframework.stereotype.Repository;
import com.wetrip.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);
}
