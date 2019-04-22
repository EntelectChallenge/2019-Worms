cppbot.o:
	g++ -I . -c -std=c++14 -fPIC ./cppbot/cppbot.cpp
all: cppbot.o bin
	g++ cppbot.o -o ./bin/cppbot.out
bin:
	mkdir bin