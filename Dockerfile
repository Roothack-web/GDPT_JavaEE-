# 使用OpenJDK 8
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制JAR文件
COPY target/product-order-system-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
