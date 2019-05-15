FROM openjdk:8u212-jre-alpine3.9

COPY ./target/game-runner-jar-with-dependencies.jar game-runner.jar
COPY ./game-runner-config.json game-runner-config.json

CMD ["/usr/bin/java", "-jar", "game-runner.jar"]
