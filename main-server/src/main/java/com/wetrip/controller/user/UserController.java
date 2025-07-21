package com.wetrip.controller.user;

import com.wetrip.dto.UserRequestDto.GenderAgeRequest;
import com.wetrip.dto.UserRequestDto.NicknameRequest;
import com.wetrip.dto.UserRequestDto.ProfileUploadRequest;
import com.wetrip.dto.UserRequestDto.TripTypeRequest;
import com.wetrip.dto.UserResponseDto;
import com.wetrip.service.S3Service;
import com.wetrip.service.user.UserService;
import com.wetrip.service.Redis.RedisOnboardingService;
import com.wetrip.user.entity.User;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/signup")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;
    private final RedisOnboardingService redisOnboardingService;

    @PostMapping("/nickname")
    public ResponseEntity<Map<String, Object>> saveNickname(
        @AuthenticationPrincipal String userId,
        @RequestBody NicknameRequest request) {

        userService.validateNickname(request.getName());
        redisOnboardingService.saveStep(Long.parseLong(userId), "nickname", request.getName());

        return ResponseEntity.ok(Map.of("message", "닉네임이 임시 저장되었습니다.", "nickname", request.getName()));
    }

    @GetMapping("/nickname")
    public ResponseEntity<Map<String, String>> getNickname(
        @AuthenticationPrincipal String userId) {

        User user = userService.findByUser(Long.parseLong(userId));

        return ResponseEntity.ok(Map.of("nickname", user.getName()));
    }

    @PostMapping("/gender_age")
    public ResponseEntity<Map<String, Object>> saveGenderAge(
        @AuthenticationPrincipal String userId,
        @RequestBody GenderAgeRequest request) {

        redisOnboardingService.saveStep(Long.parseLong(userId), "gender", request.getGender().toString());
        redisOnboardingService.saveStep(Long.parseLong(userId), "age", request.getAge().toString());

        return ResponseEntity.ok(Map.of("message", "성별과 나이가 임시 저장되었습니다.", "gender", request.getGender(), "age", request.getAge()));
    }

    @GetMapping("/gender_age")
    public ResponseEntity<Map<String, Object>> getGenderAge(
        @AuthenticationPrincipal String userId) {

        User user = userService.findByUser(Long.parseLong(userId));
        return ResponseEntity.ok(Map.of(
            "gender", user.getGender(),
            "age", user.getAge()));
    }

    @PostMapping("/trip_type")
    public ResponseEntity<Map<String, String>> saveTripType(
        @AuthenticationPrincipal String userId,
        @RequestBody TripTypeRequest request) {

        redisOnboardingService.saveStep(Long.parseLong(userId), "tripTypeId", request.getTripTypeId());

        return ResponseEntity.ok(Map.of("message", "여행 타입이 저장되었습니다."));
    }

    @GetMapping("/trip_type")
    public ResponseEntity<Map<String, Object>> getTripType(
        @AuthenticationPrincipal String userId) {

        var tripTypeId = userService.findTripTypeId(Long.parseLong(userId));
        return ResponseEntity.ok(Map.of("tripTypeId", tripTypeId));
    }

    @PostMapping("/profile_photo_url")
    public ResponseEntity<Map<String, String>> getProfileUploadUrl(
        @AuthenticationPrincipal String userId,
        @RequestBody Map<String, String> request) {

        String extension = request.get("extension");
        String prefix = "profile/" + userId;
        Duration duration = Duration.ofMinutes(10);

        Map<String, String> result = s3Service.generatePresignedUploadUrl(prefix, extension, duration);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/profile_photo_save")
    public ResponseEntity<Map<String, Object>> saveProfileUpload(
        @AuthenticationPrincipal String userId,
        @RequestBody ProfileUploadRequest request) {

        String fileName = request.getFileName();
        //클라이언트가 업로드 완료 후 최종 URL 생성
        String imageUrl = "https://wetripbucket.s3.amazonaws.com/profile/" + userId + "/" + fileName;

        redisOnboardingService.saveStep(Long.parseLong(userId), "profile", imageUrl);

        return ResponseEntity.ok(Map.of("message", "프로필이 임시 저장되었습니다.", "profileImageUrl", imageUrl));
    }

    @GetMapping("/profile_photo")
    public ResponseEntity<Map<String, String>> getProfilePhoto(
        @AuthenticationPrincipal String userId) {

        User user = userService.findByUser(Long.parseLong(userId));
        return ResponseEntity.ok(Map.of("profileImageUrl", user.getProfileImage()));
    }

    @DeleteMapping("/profile_photo")
    public ResponseEntity<Map<String, String>> deleteProfilePhoto(
        @AuthenticationPrincipal String userId) {

        User user = userService.findByUser(Long.parseLong(userId));
        String imageUrl = user.getProfileImage();

        if(imageUrl != null && imageUrl.contains("/")) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            String prefix = "profile/" + userId;

            s3Service.deleteObject(prefix, fileName);
            userService.updateProfileImage(Long.parseLong(userId), null);

        }
        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 삭제되었습니다."));
    }

    @GetMapping("/temp")  //레디스 임시저장 조회
    public ResponseEntity<Map<String, Object>> getTempOnboarding(
        @AuthenticationPrincipal String userId) {
        Long uid = Long.parseLong(userId);
        var data = redisOnboardingService.getAll(uid);
        return ResponseEntity.ok(Map.of("tempData", data));
    }

    @PostMapping("/complete")
    public ResponseEntity<UserResponseDto> complete(
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);

        // Redis에서 전체 값 꺼냄
        var data = redisOnboardingService.getAll(uid);

        userService.completeOnboarding(uid, data);
        redisOnboardingService.delete(uid);

        User updatedUser = userService.findByUser(uid);
        List<Long> tripTypeId = userService.findTripTypeId(uid);

        return ResponseEntity.ok(new UserResponseDto(updatedUser, tripTypeId));
    }

    @GetMapping("/complete")
    public ResponseEntity<UserResponseDto> getCompleteOnboarding(
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);
        User user = userService.findByUser(uid);
        List<Long> tripTypeId = userService.findTripTypeId(uid);

        return ResponseEntity.ok(new UserResponseDto(user, tripTypeId));
    }
}

