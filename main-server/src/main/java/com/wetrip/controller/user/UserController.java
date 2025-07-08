package com.wetrip.controller.user;

import com.wetrip.dto.UserRequestDto.GenderAgeRequest;
import com.wetrip.dto.UserRequestDto.NicknameRequest;
import com.wetrip.dto.UserRequestDto.TripTypeRequest;
import com.wetrip.service.S3Service;
import com.wetrip.service.user.UserService;
import com.wetrip.user.entity.User;
import java.net.URL;
import java.time.Duration;
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

    @PostMapping("/nickname")
    public ResponseEntity<Map<String, String>> saveNickname(
        @AuthenticationPrincipal String userId,
        @RequestBody NicknameRequest request) {
        userService.updateNickname(Long.parseLong(userId), request.getName());
        return ResponseEntity.ok(Map.of("message", "닉네임이 저장되었습니다."));
    }

    @PostMapping("/gender-age")
    public ResponseEntity<Map<String, String>> saveGenderAge(
        @AuthenticationPrincipal String userId,
        @RequestBody GenderAgeRequest request) {
        userService.updateGenderAge(Long.parseLong(userId), request.getGender(), request.getAge());
        return ResponseEntity.ok(Map.of("message", "성별과 나이가 저장되었습니다."));
    }

    @PostMapping("/profile-photo-url")
    public ResponseEntity<Map<String, String>> getProfileUploadUrl(
        @AuthenticationPrincipal String userId,
        @RequestBody Map<String, String> request) {

        String fileName = request.get("fileName");
        String extension = request.get("extension");

        String prefix = "profile/" + userId;
        Duration duration = Duration.ofMinutes(10);

        URL presignedUrl = s3Service.generatePresignedUploadUrl(prefix, fileName, extension, duration);

        return ResponseEntity.ok(Map.of("uploadUrl", presignedUrl.toString()));
    }

    @PostMapping("/profile-photo-save")
    public ResponseEntity<Map<String, String>> saveProfileUpload(
        @AuthenticationPrincipal String userId,
        @RequestBody Map<String, String> request) {

        String fileName = request.get("fileName");

        //클라이언트가 업로드 완료 후 최종 URL 생성
        String imageUrl = "https://wetripbucket.s3.amazonaws.com/profile/" + userId + "/" + fileName;

        userService.updateProfileImage(Long.parseLong(userId), imageUrl);

        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 저장되었습니다.",
            "profileImageUrl", imageUrl));
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<Map<String, String>> getProfilePhoto(
        @AuthenticationPrincipal String userId) {

        User user = userService.findByUser(Long.parseLong(userId));

        return ResponseEntity.ok(Map.of("profileImageUrl", user.getProfileImage()));
    }

    @DeleteMapping("/profile-photo")
    public ResponseEntity<Map<String, String>> deleteProfilePhoto(
        @AuthenticationPrincipal String userId,
        @RequestBody Map<String, String> request) {

        String fileName = request.get("fileName");
        String prefix = "profile/" + userId;

        s3Service.deleteObject(prefix, fileName);

        userService.updateProfileImage(Long.parseLong(userId), null);

        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 삭제되었습니다."));
    }

    @PostMapping("/trip-type")
    public ResponseEntity<Map<String, String>> saveTripType(
        @AuthenticationPrincipal String userId,
        @RequestBody TripTypeRequest request) {
        userService.updateTripType(Long.parseLong(userId), request.getTripTypeId());
        return ResponseEntity.ok(Map.of("message", "여행 타입이 저장되었습니다."));
    }
}
