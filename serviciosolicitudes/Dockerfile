FROM eclipse-temurin:21-jdk
WORKDIR /app
# ¡ATENCIÓN! Verificá que este nombre coincida con el archivo en tu carpeta /target
COPY target/serviciosolicitudes-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]