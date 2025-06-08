# 1️⃣ JDK 21 기반 이미지 사용
FROM openjdk:21-jdk

# 2️⃣ 작업 디렉토리 설정
WORKDIR /app

# 3️⃣ 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY build/libs/*.jar app.jar

# 4️⃣ 8080 포트 개방
EXPOSE 8080

# 5️⃣ 컨테이너 실행 시 Java 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]