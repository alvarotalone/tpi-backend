FROM eclipse-temurin:21-jdk
WORKDIR /app
# Verificar que este nombre coincida con el archivo en tu carpeta /target
COPY target/serviciocamiones-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]