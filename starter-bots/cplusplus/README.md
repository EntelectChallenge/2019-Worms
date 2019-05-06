# C++ bot for Entelect Challenge 2019

This container includes components required to compile and run a c++ starter bot.

## Requirements :

- GCC 5.4 or newer (currently built on 5.4 and 6.3)
- Visual Studio 2017 solution supplied.
- RapidJSON included for JSON support


## Compiling c++ Project :

- Supplied makefile will compile the starter bot in linux. 
  - Any additional files added to the project will also need to be added to the makefile.
- The visual studio solution will compile a debug windows executable.


## Running the Bot :
- executable will compile to ./bin/cppbot.exe (linux build also outputs cppbot.exe so that bot.json file doesn't need to change)
- bot.json currently points to ./bin/cppbot.exe

## Debugging the bot:
- There is a debug.cpp file included that can be used in Visual Studio stop execution on bot start, allowing you to attach a debugger before continuing. game-runner-config.json will need to be updated to have amuch larger request-timeout-ms and max-runtime-ms.
