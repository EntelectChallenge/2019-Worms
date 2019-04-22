all: cppbot.o bin
	g++ cppbot.o -o ./bin/cppbot.out
cppbot.o:
	g++ -I . -c -std=c++14 ./cppbot/cppbot.cpp
bin:
	mkdir bin