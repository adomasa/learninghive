./gradlew build && java -jar build/libs/*.jar &&
sudo docker build --build-arg JAR_FILE=build/libs/*.jar -t backend .
