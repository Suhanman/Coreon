# 1단계: Maven 빌드 (mvnw가 없으니 maven 이미지 사용)
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY . .
RUN mvn -DskipTests clean package

# 2단계: 런타임 이미지
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /build/target/*.jar /app/app.jar

# (선택) 외부에서 JAVA_OPTS 주입 가능하게
ENV JAVA_OPTS=""

# 최종 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
