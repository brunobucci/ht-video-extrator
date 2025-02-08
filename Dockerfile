# Usa uma imagem base com Maven e JDK 21 para compilar o projeto
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para dentro do container
COPY . .

# Baixa as dependências e compila o projeto
RUN mvn clean package -DskipTests

# Criando uma segunda etapa para a imagem final, mais leve
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia o JAR gerado da etapa de build
COPY --from=build /app/target/ht-video-extrator-*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8082

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]
