# Java Docker Container

This container includes components required to compile and run a java project.

Requirements :

- Java Runtime Environment (JRE), v1.8
- Java Development Kit (JDK), open-jdk-8
- Maven, v3.3.9

Compiling Java Project :

- First ensure the project has a pom.xml, which details for the configuration of the project and is used by maven to build it.
- Command to execute, to build project with dependencies into an executable file.
	* mvn dependency:copy-dependencies package

Running the Bot :

- Command to run the bot:
	* java -jar <path/to/jarfile>
	
Reference Material:

- Java Tutorial
	* https://www.tutorialspoint.com/java/
- Getting started with Maven
	* https://maven.apache.org/guides/getting-started/
- Maven in 5 Minutes
	* https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html