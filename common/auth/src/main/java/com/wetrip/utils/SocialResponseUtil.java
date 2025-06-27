package com.wetrip.utils;

import com.wetrip.user.entity.User;

import java.time.LocalDate;

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
            System.err.println("parsing error: " + ageRange);
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
            System.err.println("parsing error" + age);
        }
        return 0;
    }

    public static LocalDate parseBirthday(String birthday, String birthyear) {
        try {
            if (birthday != null && birthday.length() == 5 && birthday.contains("-")) { // MM-DD 형식
                String[] parts = birthday.split("-");
                int year = birthyear != null ? Integer.parseInt(birthyear) : 1990;
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                return LocalDate.of(year, month, day);
            } else if (birthday != null && birthday.length() == 4) { // MMDD 형식
                int year = birthyear != null ? Integer.parseInt(birthyear) : 1990;
                int month = Integer.parseInt(birthday.substring(0, 2));
                int day = Integer.parseInt(birthday.substring(2, 4));
                return LocalDate.of(year, month, day);
            } else if (birthday != null && birthday.length() == 10) { // YYYY-MM-DD
                return LocalDate.parse(birthday);
            }
        } catch (Exception e) {
            System.err.println("parsing error " + birthday);
        }
        return null;
    }

    public static LocalDate parseBirthday(String birthday) {
        try {
            if (birthday != null) {
                return LocalDate.parse(birthday);
            }
        } catch (Exception e) {
            System.err.println("parsing error" + birthday);
        }
        return null;
    }

    public static User.Gender parseGender(String gender) {
        if (gender == null) return User.Gender.none;
        return switch (gender.toLowerCase()) {
            case "female", "f" -> User.Gender.female;
            case "male", "m" -> User.Gender.male;
            default -> User.Gender.none;
        };
    }
}
