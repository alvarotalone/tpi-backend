FROM eclipse-temurin:21-jdk
WORKDIR /app
# Verificar que este nombre coincida con el archivo en tu carpeta /target
COPY target/serviciocamiones-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]