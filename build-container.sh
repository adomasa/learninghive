./gradlew build && java -jar build/libs/gs-spring-boot-docker-0.1.0.jar &&
sudo docker build --build-arg JAR_FILE=build/libs/*.jar -t backend .
