# Build Stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
# 테스트는 제외하고 빌드 (시간 단축)
RUN ./gradlew clean build -x test

# Run Stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경변수 처리를 위해 실행 시점에 주입받음
ENTRYPOINT ["java", "-jar", "app.jar"]
