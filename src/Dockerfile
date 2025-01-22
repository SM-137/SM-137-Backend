# Base image: JDK 17
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY target/your-app.jar app.jar

# 컨테이너 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]