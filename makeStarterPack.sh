#!/bin/bash

mkdir starter-pack

cd game-engine
engineJarName=$(./gradlew fullJarName -q --console=plain)
cp "build/libs/$engineJarName" ../starter-pack
cp default-config.json ../starter-pack/game-config.json
cd ..

cd game-runner
cp makefile run.bat ../starter-pack
cp starter-pack-runner-config.json ../starter-pack/game-runner-config.json
cp target/*with-dependencies.jar ../starter-pack
cd ..

cp -r reference-bot starter-pack/
cp -r starter-bots starter-pack/

cd starter-pack
sed -i "s/game-engine\.jar/.\/$engineJarName/g" game-runner-config.json
sed -i "s/target/./g" run.bat makefile
cd ..

zip -r starter-pack.zip starter-pack/*


