# 1단계: Maven 빌드
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

COPY . .
RUN chmod +x mvnw || true
RUN ./mvnw clean package -DskipTests

# 2단계: 런타임 이미지
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /build/target/*.jar app.jar

# (선택) 외부에서 JAVA_OPTS 주입 가능하게
ENV JAVA_OPTS=""

# 최종 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
