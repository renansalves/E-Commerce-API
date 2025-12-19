
# Etapa de build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia gradle e build scripts
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# Como usa Alpine, garanta permissões (se necessário):
RUN chmod +x gradlew

# Build do jar (pode usar --no-daemon e cache de dependências)
RUN ./gradlew clean bootJar --no-daemon

# Etapa de runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


