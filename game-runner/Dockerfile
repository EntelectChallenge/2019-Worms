FROM openjdk:8u181-jre

COPY ./target/game-runner-3.0.2-jar-with-dependencies.jar game-runner.jar
COPY ./game-runner-config.json game-runner-config.json

CMD ["/usr/bin/java", "-jar", "game-runner.jar"]


# docker run --env MATCH_ID=738de844-ffe2-40fb-8ff3-5fa80a98243a --env SEED=50 --env PLAYER_A=entries/main/5b1e26344a10917f59c7f329/5b94435d4a1091d99c6baf17/binaries/5b94435d4a1091d99c6baf17.zip --env PLAYER_B=entries/main/1e81d9d9e2b19a201caefc9f36f6ea61\5e39196bf45e213fc775e1f3cf400f3d/mlem.zip game-runner

# docker run --env MATCH_ID=738de844-ffe2-40fb-8ff3-5fa80a98243a --env SEED=50 --env PLAYER_A=entries/main/1e81d9d9e2b19a201caefc9f36f6ea61\5e39196bf45e213fc775e1f3cf400f3d/mlem.zip --env PLAYER_B=entries/main/1e81d9d9e2b19a201caefc9f36f6ea61\5e39196bf45e213fc775e1f3cf400f3d/mlem.zip game-runner

# docker run --env MATCH_ID=738de844-ffe2-40fb-8ff3-5fa80a98243a --env SEED=50 --env PLAYER_A=entries/main/1e81d9d9e2b19a201caefc9f36f6ea61\5e39196bf45e213fc775e1f3cf400f3d/python3.zip --env PLAYER_B=entries/main/1e81d9d9e2b19a201caefc9f36f6ea61\5e39196bf45e213fc775e1f3cf400f3d/python3.zip --env GAME_ENGINE=engine.zip entelectchallenge/game-runner