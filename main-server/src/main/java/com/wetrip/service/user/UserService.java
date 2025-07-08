package com.wetrip.service.user;

import com.wetrip.user.entity.UserTripType;
import com.wetrip.user.repository.UserTripTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.wetrip.user.entity.User;
import com.wetrip.user.repository.UserRepository;

import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTripTypeRepository userTripTypeRepository;

    public User findByUser(final Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다"));
    }

    public void validateNickname(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }

        if (!name.matches("^[가-힣a-zA-Z0-9]{1,10}$")) {
            throw new IllegalArgumentException("띄어쓰기 없이 한글, 영문, 숫자 10자 이내로 가능합니다.");
        }

        if (userRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }

    @Transactional
    public void updateNickname(Long userId, String name) {
        validateNickname(name);
        User user = findByUser(userId);
        user.setName(name);
    }

    @Transactional
    public void updateGenderAge(Long userId, User.Gender gender, Integer age) {
        User user = findByUser(userId);
        user.setGender(gender);
        user.setAge(age);
    }

    @Transactional
    public void updateTripType(Long userId, List<Long> tripTypeId) {
        //이전 여행 타입 전부 삭제(선택지 덮어쓰기)
        userTripTypeRepository.deleteByUserId(userId);

        //새로운 여행 타입 추가
        List<UserTripType> userTripTypes = tripTypeId.stream()
            .map(id -> UserTripType.builder()
                .userId(userId)
                .tripTypeId(id)
                .build())
            .toList();

        userTripTypeRepository.saveAll(userTripTypes);
    }

    @Transactional
    public void updateProfileImage(Long userId, String profileImage) {
        User user = findByUser(userId);
        user.setProfileImage(profileImage);  //userdto 리턴?
    }
}
