# 1. Usamos la imagen base de Java 21
FROM eclipse-temurin:21-jdk

# 2. Definimos el directorio de trabajo
WORKDIR /app

# 3. Copiamos el .jar
# (Asegurate de que en tu carpeta target el archivo se llame así)
COPY target/api-gateway-1.0.0.jar app.jar

# 4. Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]