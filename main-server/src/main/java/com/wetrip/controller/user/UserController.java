package com.wetrip.controller.user;

import com.wetrip.dto.UserRequestDto.GenderAgeRequest;
import com.wetrip.dto.UserRequestDto.NicknameRequest;
import com.wetrip.dto.UserRequestDto.ProfileUploadRequest;
import com.wetrip.dto.UserRequestDto.TripTypeRequest;
import com.wetrip.dto.UserResponseDto;
import com.wetrip.exception.user.AgeMissingException;
import com.wetrip.exception.user.GenderMissingException;
import com.wetrip.exception.user.InvalidTripTypeException;
import com.wetrip.service.S3Service;
import com.wetrip.service.user.UserService;
import com.wetrip.service.Redis.RedisOnboardingService;
import com.wetrip.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Onboarding", description = "사용자 온보딩 관련 API")
@RestController
@RequestMapping("/api/auth/signup")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;
    private final RedisOnboardingService redisOnboardingService;

    @Operation(summary = "닉네임 저장", description = "회원가입 과정에서 닉네임을 임시 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "닉네임 저장 성공"),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 닉네임")
    })

    @PostMapping("/nickname")
    public ResponseEntity<Map<String, Object>> saveNickname(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId,
        @RequestBody NicknameRequest request) {

        userService.validateNickname(request.getName());
        redisOnboardingService.saveStep(Long.parseLong(userId), "nickname", request.getName());

        return ResponseEntity.ok(Map.of("message", "닉네임이 임시 저장되었습니다.", "nickname", request.getName()));
    }

    @Operation(summary = "닉네임 임시 조회", description = "사용자의 임시 저장된 닉네임을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "닉네임 조회 성공")
    @GetMapping("/nickname")
    public ResponseEntity<Map<String, String>> getReidsNickname(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {

        String nickname = (String) redisOnboardingService.getStep(Long.parseLong(userId), "nickname");

        return ResponseEntity.ok(Map.of("nickname", nickname));
    }

    @Operation(summary = "성별과 나이 저장", description = "회원가입 과정에서 성별과 나이를 임시 저장합니다.")
    @ApiResponse(responseCode = "200", description = "성별과 나이 저장 성공")
    @PostMapping("/gender_age")
    public ResponseEntity<Map<String, Object>> saveGenderAge(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId,
        @RequestBody GenderAgeRequest request) {

        userService.validateGender(request.getGender());
        userService.validateAge(request.getAge());

        redisOnboardingService.saveStep(Long.parseLong(userId), "gender", request.getGender().toString());
        redisOnboardingService.saveStep(Long.parseLong(userId), "age", request.getAge().toString());

        return ResponseEntity.ok(Map.of("message", "성별과 나이가 임시 저장되었습니다.", "gender", request.getGender(), "age", request.getAge()));
    }

    @Operation(summary = "성별과 나이 임시 조회", description = "사용자의 임시 저장된 성별과 나이를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성별과 나이 조회 성공")
    @GetMapping("/gender_age")
    public ResponseEntity<Map<String, Object>> getRedisGenderAge(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);

        String gender = (String) redisOnboardingService.getStep(uid, "gender");
        String age = (String) redisOnboardingService.getStep(uid, "age");

        return ResponseEntity.ok(Map.of("gender", gender, "age", age));
    }

    @Operation(summary = "여행 타입 저장", description = "회원가입 과정에서 사용자의 여행 타입을 저장합니다.")
    @ApiResponse(responseCode = "200", description = "여행 타입 저장 성공")
    @PostMapping("/trip_type")
    public ResponseEntity<Map<String, String>> saveTripType(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId,
        @RequestBody TripTypeRequest request) {

        userService.validateTripType(request.getTripTypeId());
        redisOnboardingService.saveStep(Long.parseLong(userId), "tripTypeId", request.getTripTypeId());

        return ResponseEntity.ok(Map.of("message", "여행 타입이 저장되었습니다."));
    }

    @Operation(summary = "여행 타입 임시 조회", description = "사용자의 임시 저장된 여행 타입을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "여행 타입 조회 성공")
    @GetMapping("/trip_type")
    public ResponseEntity<Map<String, Object>> getTripType(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);
        Object tripTypeObj = redisOnboardingService.getStep(uid, "tripTypeId");

        if (tripTypeObj instanceof List<?> tripTypeList) {
            return ResponseEntity.ok(Map.of("tripTypeId", tripTypeList));
        } else {
            return ResponseEntity.ok(Map.of("tripTypeId", List.of()));
        }
 }

    @Operation(summary = "프로필 사진 업로드 URL 발급", description = "프로필 사진 업로드를 위한 사전 서명된 URL을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 URL 발급 성공")
    @PostMapping("/profile_photo_url")
    public ResponseEntity<Map<String, String>> getProfileUploadUrl(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId,
        @RequestBody Map<String, String> request) {

        String extension = request.get("extension");
        String prefix = "profile/" + userId;
        Duration duration = Duration.ofMinutes(10);

        Map<String, String> result = s3Service.generatePresignedUploadUrl(prefix, extension, duration);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "프로필 사진 저장", description = "회원가입 과정에서 업로드된 프로필 사진 정보를 임시 저장합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 사진 저장 성공")
    @PostMapping("/profile_photo_save")
    public ResponseEntity<Map<String, Object>> saveProfileUpload(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId,
        @RequestBody ProfileUploadRequest request) {

        String fileName = request.getFileName();
        //클라이언트가 업로드 완료 후 최종 URL 생성
        String imageUrl = "https://wetripbucket.s3.amazonaws.com/profile/" + userId + "/" + fileName;

        redisOnboardingService.saveStep(Long.parseLong(userId), "profile", imageUrl);

        return ResponseEntity.ok(Map.of("message", "프로필이 임시 저장되었습니다.", "profileImageUrl", imageUrl));
    }

    @Operation(summary = "프로필 사진 조회", description = "사용자의 임시 저장된 프로필 사진을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 사진 조회 성공")
    @GetMapping("/profile_photo")
    public ResponseEntity<Map<String, String>> getRedisProfilePhoto(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);
        String profileImageUrl = (String) redisOnboardingService.getStep(uid, "profile");

        return ResponseEntity.ok(Map.of("profileImageUrl", profileImageUrl != null ? profileImageUrl : ""));
    }

    @Operation(summary = "프로필 사진 삭제", description = "사용자의 임시 저장된 프로필 사진을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 사진 삭제 성공")
    @DeleteMapping("/profile_photo")
    public ResponseEntity<Map<String, String>> deleteRedisProfilePhoto(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);
        String imageUrl = (String) redisOnboardingService.getStep(uid, "profile");

        if (imageUrl != null && imageUrl.contains("/")) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            String prefix = "profile/" + userId;

            // S3에서 객체 삭제
            s3Service.deleteObject(prefix, fileName);

            // Redis에서 profile 정보 삭제 또는 null 처리
            redisOnboardingService.saveStep(uid, "profile", null);
        }

        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 삭제되었습니다."));
    }

    @Operation(summary = "임시 저장된 온보딩 정보 조회", description = "Redis에 임시 저장된 온보딩 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "임시 저장 정보 조회 성공")
    @GetMapping("/temp")  //레디스 임시저장 조회
    public ResponseEntity<Map<String, Object>> getTempOnboarding(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {
        Long uid = Long.parseLong(userId);
        var data = redisOnboardingService.getAll(uid);
        return ResponseEntity.ok(Map.of("tempData", data));
    }

    @Operation(summary = "온보딩 완료", description = "임시 저장된 온보딩 정보를 실제 DB에 저장하고 온보딩을 완료합니다.")
    @ApiResponse(responseCode = "200", description = "온보딩 완료 성공")
    @PostMapping("/complete")
    public ResponseEntity<UserResponseDto> complete(
        @Parameter(description = "사용자 ID", hidden = true)
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

    @Operation(summary = "완료된 온보딩 정보 조회", description = "완료된 온보딩 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "온보딩 정보 조회 성공")
    @GetMapping("/complete")
    public ResponseEntity<UserResponseDto> getCompleteOnboarding(
        @Parameter(description = "사용자 ID", hidden = true)
        @AuthenticationPrincipal String userId) {

        Long uid = Long.parseLong(userId);
        User user = userService.findByUser(uid);
        List<Long> tripTypeId = userService.findTripTypeId(uid);

        return ResponseEntity.ok(new UserResponseDto(user, tripTypeId));
    }
}

