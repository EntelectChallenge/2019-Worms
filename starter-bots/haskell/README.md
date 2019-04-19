# Haskell Starter Bot
Haskell is a purely functional programming language.  You can find out
more about Haskell [here](https://www.haskell.org/).

## Environment Requirements
Install the [Haskell Platform](https://www.haskell.org/platform/) and
ensure that the `stack` executable is on the path.

## Building
Simply run:

```
stack install --local-bin-path bin
```

to build the binary and put it into a folder in the root of the
project called `bin`.

## Running
Haskell creates native binaries so you can simply run:

```
./bin/haskell-bot-exe
```

from the command line to invoke the bot program.

