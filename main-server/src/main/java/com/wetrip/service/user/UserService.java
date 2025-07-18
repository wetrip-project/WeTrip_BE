package com.wetrip.service.user;

import com.wetrip.user.entity.UserTripType;
import com.wetrip.user.enums.Gender;
import com.wetrip.user.repository.UserTripTypeRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.wetrip.user.entity.User;
import com.wetrip.user.repository.UserRepository;

import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        if (!StringUtils.hasText(name)) {
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
    public User updateNickname(Long userId, String name) {
        validateNickname(name);
        User user = findByUser(userId);
        user.setName(name);
        return user;
    }

    @Transactional
    public User updateGenderAge(Long userId, Gender gender, Integer age) {
        User user = findByUser(userId);
        user.setGender(gender);
        user.setAge(age);
        return user;
    }

    @Transactional
    public void updateTripType(Long userId, List<Long> tripTypeId) {
        //이전 여행 타입 전부 삭제(선택지 덮어쓰기)
        userTripTypeRepository.deleteByUserId(userId);

        //새로운 여행 타입 추가
        if (!CollectionUtils.isEmpty(tripTypeId)) {
            List<UserTripType> userTripTypes = tripTypeId.stream()
                .map(id -> UserTripType.builder()
                    .userId(userId)
                    .tripTypeId(id)
                    .build())
                .toList();
            userTripTypeRepository.saveAll(userTripTypes);
        }
    }

    public List<Long> findTripTypeId(Long userId) {
        return userTripTypeRepository.findByUserId(userId).stream()
            .map(UserTripType::getTripTypeId)
            .toList();
    }

    @Transactional
    public User updateProfileImage(Long userId, String profileImage) {
        User user = findByUser(userId);
        user.setProfileImage(profileImage);
        return user;
    }

    @Transactional
    public void completeOnboarding(Long userId, Map<Object, Object> data) {
        User user = findByUser(userId);

        user.setName(data.get("nickname").toString());
        user.setGender(Gender.valueOf(data.get("gender").toString()));
        user.setAge(Integer.parseInt(data.get("age").toString()));

        Object profile = data.get("profile");
        if (profile != null) {
            user.setProfileImage(data.get("profile").toString());
        }

        List<Long> tripTypeId = castList(data.get("tripTypeId"));
        updateTripType(userId, tripTypeId == null ? List.of() : tripTypeId);
    }

    @SuppressWarnings("unchecked")
    private List<Long> castList(Object raw) {
        if (raw == null) return null;
        if (raw instanceof List<?> list) {
            return list.stream()
                .map(v -> {
                    if (v instanceof Number n) return n.longValue();
                    return Long.parseLong(v.toString());
                })
                .toList();
        }
        // 단일 값이 들어온 경우
        return List.of(Long.parseLong(raw.toString()));
    }


    private String getRequired(Map<Object, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("필수값 '" + key + "'가 누락되었습니다.");
        }
        return value.toString();
    }
}
