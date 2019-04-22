all: cppbot.o
	g++ cppbot.o -o ./bin/cppbot.out
cppbot.o: bin
	g++ -I . -c ./cppbot/cppbot.cpp
bin:
	mkdir bin