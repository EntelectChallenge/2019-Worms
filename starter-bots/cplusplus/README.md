# c++ Docker Container

This container includes components required to compile and run a c++ project. The start bot was a c++ version of the supplies javascript reference bot

## Requirements :

- GCC 5.4 or newer (currently built on 5.4 and 6.3)
- Visual Studio 2017 solution supplied.
- RapidJSON included for JSON support


## Compiling c++ Project :

- Supplied makefile will compile the starter bot. 
  - Any additional files added to the project will also need to be added to the makefile.


## Running the Bot :
- executable will compile to ./bin/cppbot.exe (linux build also outputs cppbot.exe so that bot.json file doesn't need to change)
- bot.json currently points to ./bin/cppbot.exe
