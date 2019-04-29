all: cppbot.o bin
	g++ cppbot.o -o ./bin/cppbot.exe
cppbot.o:
	g++ -I . -c -std=c++14 -fPIC ./cppbot/cppbot.cpp
bin:
	mkdir bin

