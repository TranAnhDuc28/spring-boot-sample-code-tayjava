# chỉ định dowload jdk nào
FROM openjdk:17

# trỏ đến đường dẫn file jar để docker đóng gói
#ARG FILE_JAR=target/spring-boot-sample-code-tayjava-0.0.1-SNAPSHOT.jar
ARG FILE_JAR=target/*.jar

# Thêm vào docker
ADD ${FILE_JAR} api-service.jar

# chạy
ENTRYPOINT ["java", "-jar", "api-service.jar"]

# truy suất vào container trong ứng dụng
EXPOSE 80