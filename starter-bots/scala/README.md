# Scala starter bot

## Environment requirements

Install the Java SE Development Kit 8 for your environment here: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Make sure JAVA_HOME system variable is set, Windows 10 tutorial here: https://www.mkyong.com/java/how-to-set-java_home-on-windows-10/

Install IntelliJ IDEA here: https://www.jetbrains.com/idea/download/
The community edition is free.

## Building

Make your modifications to the starter bot using IntelliJ. Once you are happy with your changes, package your bot by running "sbt assembly" from your terminal.
This  will create a .jar file in the folder called "target/scala-2.12/". The file will be called "scala-sample-bot-jar-with-dependencies.jar".

## Running 

To run the bot, copy the file "scala-sample-bot-jar-with-dependencies.jar" to a different location. Then go to the starter-pack and edit the config.json file accordingly.
Then run the "run.bat" file on windows or the "run.sh" file for unix.