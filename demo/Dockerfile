# Etapa 1: build da aplicação com Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e gera o .jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: imagem final, mais leve, só com o JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]