# JAR 파일을 실행할 기본 이미지 선택
FROM openjdk:17-jdk-alpine

# JAR 파일을 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 타임존 등 환경 설정
ENV TZ=Asia/Seoul

# 실행 명령어
ENTRYPOINT ["java","-jar","/app.jar"]
