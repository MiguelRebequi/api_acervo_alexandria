# Etapa 1: Build da aplicação com Maven e Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências (otimiza o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e faz o empacotamento pulando os testes unitários
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final leve apenas com o JRE para execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o arquivo .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Porta padrão exposta pelo Spring Boot
EXPOSE 8080

# Comando para iniciar a API
ENTRYPOINT ["java", "-jar", "app.jar"]