# Game Engine

## Building the project

To build this project you can use the gradle wrapper that is distributed with it. 
In the root directory of the project simply run `gradlew build`.

### Prerequisites

JDK 1.8
  - [Windows](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - [Linux](https://openjdk.java.net/install/)
  
No gradle installation is required.

### IDE Support
Due to the improved gradle support, we recommend using IntelliJ Idea 2018.3 or later 

## Language

The game engine is built in Kotlin. It uses the Kotlin multi-platform plugin to compile the core engine to both the jvm and javascript. 
 
The project consists of 3 source sets:

### game-engine-core
This source set is shared between all platforms and cannot contain any platform specific code or dependencies.  

All game logic lives in this module. 

### game-engine-jvm
This source set contains all jvm-specific code. This includes implementing all interfaces that are required to integrate 
with the game runner.

This module contains many delegate classes that implement the game runner interfaces and then delegate the game logic to the appropriate
classes in the core source set.

The main entry point for the game runner is through the `WormsGameBoostrapper` class. 

#### Output
The jvm module builds two jars inside the `build/lib` directory: 
* ec-2019-game-engine-jvm - Standard jar that contains all the compiled project classes
* ec-2019-game-engine-jvm-full - A fat jar that includes the project classes as well as dependencies
  
### game-engine-js
This source set contains all js-specific code. It is being used by the challenge team to experiment with the game engine on different platforms.

This module provides the `GameRunner` class as the main entry point for javascript interop. 

#### Output
The js module builds to two jars and a npm package:
* The ec-2019-game-engine-js jar in the `build/lib` folder contains the compiled `ec-2019-game-engine.js` files as well 
as `kjsm` (Kotlin JavaScript Meta files) for all project classes
* The ec-2019-game-engine-js-full jar in the `build/lib` folder contains the compiled `ec-2019-game-engine.js` files, the `kotlin.js` file it is dependent on, 
 as well as `kjsm` (Kotlin JavaScript Meta files) for all project classes and dependencies
* The `build/package` directory contains a package.json and the `ec-2019-game-engine.js` file and can be installed as a 
 npm package. *This package has not been published anywhere, the directory can simply be installed as an npm package* 

## Documentation 
### Gradle
* The gradle docs can be found at [docs.gradle.org](https://docs.gradle.org/current/userguide/userguide.html)
* To get started with the gradle-kotlin buildscript, read the [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
* The documentation for the gradle-jacoco plugin (used to enforce test coverage) can be found at [ocs.gradle.org](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
* The documentation for the shadow-jar plugin (used to build the fat jars) can be found at [imperceptiblethoughts.com](https://imperceptiblethoughts.com/shadow/introduction/)

### Kotlin
 * To get started with Kotlin, the reference at [kotlinlang.org](https://kotlinlang.org/docs/reference/) is a great resource
 * You can read the overview of kotlin multiplatform programming [here](https://kotlinlang.org/docs/reference/multiplatform.html)
 * A guide to using the multiplatform plugin with gradle is available [here](https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html)