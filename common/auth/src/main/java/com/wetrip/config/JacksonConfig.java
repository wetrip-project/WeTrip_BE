package com.wetrip.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"; // 모든 LocalDateTime에 동일하게 적용될 포맷
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      DATETIME_FORMAT); // DATETIME_FORMAT로 DateTimeFormatter 인스턴스 생성 -> LocalDateTime 직렬화에 적용

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    return builder -> {
      builder.simpleDateFormat(DATETIME_FORMAT); // 기본 Date 타입에 적용되는 포맷 지정
      builder.serializers(
          new LocalDateTimeSerializer(DATE_TIME_FORMATTER)); // LocalDateTime에 대한 직렬화 포맷 지정
      builder.featuresToDisable(
          SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜,시간을 timestamp 숫자가 아닌 문자열로 출력하도록 설정
    };
  }
}
