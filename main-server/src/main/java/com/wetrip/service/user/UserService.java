package com.wetrip.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.wetrip.user.entity.User;
import com.wetrip.user.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다"));
    }
}
