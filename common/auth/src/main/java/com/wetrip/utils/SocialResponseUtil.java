package com.wetrip.utils;

import com.wetrip.user.entity.User;
import com.wetrip.user.enums.Gender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocialResponseUtil { // 로그인 과정에서 null-safe, 기본 값, 파싱 로직 분리

  public static String safeString(Object obj) {
    return obj != null ? obj.toString() : null;
  }

  public static String safeStringOrDefault(Object obj, String defaultValue) {
    return obj != null ? obj.toString() : defaultValue;
  }

  public static Integer parseAgeFromRange(String ageRange) {
    try {
      if (ageRange != null && ageRange.contains("~")) {
        String[] parts = ageRange.split("~");
        return (Integer.parseInt(parts[0]) + Integer.parseInt(parts[1])) / 2;
      }
    } catch (Exception e) {
      log.error("Failed to parse age range: {}", ageRange, e);
    }
    return 0;
  }

  public static Integer parseAge(String age) {
    try {
      if (age != null && age.contains("-")) {
        String[] parts = age.split("-");
        return (Integer.parseInt(parts[0]) + Integer.parseInt(parts[1])) / 2;
      } else if (age != null) {
        return Integer.parseInt(age);
      }
    } catch (Exception e) {
      log.error("Failed to parse age: {}", age, e);
    }
    return 0;
  }

  public static LocalDate parseBirthday(String birthday, String birthyear) {
    if (birthday == null) {
      return null;
    }

    try {
      if (birthday.length() == 10) { // YYYY-MM-DD
        return LocalDate.parse(birthday, DateTimeFormatter.ISO_LOCAL_DATE);
      } else if (birthday.length() == 5 && birthday.contains("-")) { // MM-DD 형식
        String[] parts = birthday.split("-");
        int year = birthyear != null ? Integer.parseInt(birthyear) : 1990;
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        return LocalDate.of(year, month, day);
      } else if (birthday.length() == 4) { // MMDD 형식
        int year = birthyear != null ? Integer.parseInt(birthyear) : 1990;
        int month = Integer.parseInt(birthday.substring(0, 2));
        int day = Integer.parseInt(birthday.substring(2, 4));
        return LocalDate.of(year, month, day);
      }
    } catch (DateTimeParseException | NumberFormatException e) {
      log.error("Failed to parse birthday: birthday={}, birthyear={}", birthday, birthyear, e);
    }
    return null;
  }

  public static LocalDate parseBirthday(String birthday) {
    if (birthday == null) {
      return null;
    }
    try {
      return LocalDate.parse(birthday, DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (DateTimeParseException e) {
      log.error("Failed to parse birthday: {}", birthday, e);
    }
    return null;
  }

  public static Gender parseGender(String gender) {
    if (gender == null) {
      return Gender.none;
    }
    return switch (gender.toLowerCase()) {
      case "female", "f" -> Gender.female;
      case "male", "m" -> Gender.male;
      default -> Gender.none;
    };
  }

  public static String safeStringFromMap(Map<String, Object> map, String key) {
    if (map == null || !map.containsKey(key)) {
      return null;
    }
    return safeString(map.get(key));
  }
}

