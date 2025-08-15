# 1단계: 빌드 환경
FROM gradle:7.6-jdk17 AS builder
WORKDIR /app

# 의존성 캐시 단계
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon

# 소스 전체 복사 (이 시점 이후 변경 시 무조건 빌드 다시)
COPY . .

# 최신 jar 빌드
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 런타임 환경
FROM eclipse-temurin:17-jre
WORKDIR /app

# jar 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
