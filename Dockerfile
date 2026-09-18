FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /tests
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
CMD ["mvn", "test"]
