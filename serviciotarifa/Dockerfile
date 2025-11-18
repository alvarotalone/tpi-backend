# 1. Usamos la imagen base de Java 21
FROM eclipse-temurin:21-jdk

# 2. Definimos el directorio de trabajo
WORKDIR /app

# 3. Copiamos el .jar (asegurate que este sea el nombre exacto en tu carpeta target)
COPY target/serviciotarifa-1.0.0.jar app.jar

# 4. Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]