FROM entelectchallenge/base:2019

RUN apt-get update -y

RUN apt-get install gcc -y


# Set the working directory to /app
WORKDIR /cppbot

# Copy the current directory contents into the container at /app
COPY . /cppbot

RUN cd /cppbot

run make

RUN ls