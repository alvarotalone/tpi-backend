# 1. Usamos la imagen oficial de Eclipse Temurin (Java 21)
FROM eclipse-temurin:21-jdk

# 2. Definimos el directorio de trabajo
WORKDIR /app

# 3. Copiamos el jar
COPY target/serviciodepositos-1.0.0.jar app.jar

# 4. Ejecutamos
ENTRYPOINT ["java", "-jar", "app.jar"]