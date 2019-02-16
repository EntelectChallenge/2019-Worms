# game-engine

Game Engine for EC 2019

## Project Structure

The bulk of this project is written in Kotlin to facilitate compilation to javascript or the JVM. 

### Game Engine JVM
This module is the game engine that the game runner interfaces with. It includes the Game Engine Interfaces java dependency and creates delegates to the core module where necessary. 

### Game Engine Web
This module compiles the game-engine to javascript for inclusion in the website. 

See: https://kotlinlang.org/docs/tutorials/javascript/getting-started-gradle/getting-started-with-gradle.html 

### Game Engine Core
This module contains the core game logic and is included in the source roots of both the jvm and the js modules. It should be **platform independent**.

* Any platform specific code should only have an interface defined in this module and  be implemented in both the JVM and the JS modules.
* No platform specific libraries can be included 

To verify that there are no platform specific code in this module you can ignore one of the other module in your IDE.
* Ignore the game-engine-jvm module to eliminate jvm-specific code
* Ignore the game-engine-web module to eliminate js-specific code

### Tests
Test for the core game engine is included only in the jvm module (See `build.gradle`). This means tests can be written using Junit and Mockito and does not need to be compatible with the web module. 