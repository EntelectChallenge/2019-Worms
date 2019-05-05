# Purely Functional Scala Starter Bot
Scala is a multi-paradigm language.  It supports both OO and FP and
includes interesting language design features such as the uniform
access principle and expression based control structures (such as
blocks and conditionals).

Scala provides a lot of flexibility to the programmer.  One can pick
and choose techniques as one chooses.  Naturally there are many ways
to skin a cat when using Scala.

A starter bot, written in Scala, exists already.  This bot adopts a
different approach to the design than that of the other Scala bot in
that this bot is *purely functional*.

## Environment Requirements
1. Install the `JDK`.  You can find it
   [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html)
   for the official Oracle release and
   [here](https://jdk.java.net/12/) for the `OpenJDK` release.
2. Ensure that the `JAVA_HOME` variable is exported in your
   environment and points at the root of the `JDK` installation.
3. Install `sbt`.  You can download it
   [here](https://www.scala-sbt.org/).

## Building and Running
Simply run `sbt assembly` from the root of the start bot project.  It
will produce a jar file in the appropriate directory for the game
runner to interpret.  If you'd like to run it manually then you may do
so with the following command:

```
java -jar target/scala-2.12/scala-sample-bot-jar-with-dependencies.jar
```

