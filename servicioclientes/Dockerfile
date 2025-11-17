FROM eclipse-temurin:21-jdk
WORKDIR /app
# ¡ATENCIÓN! Verificá que este nombre coincida con el archivo en tu carpeta /target
COPY target/servicioclientes-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]