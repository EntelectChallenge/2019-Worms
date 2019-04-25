# Rust starter bot

Rust is a systems programming language, giving programmers the low
level control that they would usually associate with a programming
langauge like C or C++, but modern high level programming features.

Rust is a compiled language, which compiles to a platform-specific
binary.

## Environment Setup

The Rust compiler toolchain can be downloaded from the Rust project
website.

https://www.rust-lang.org/en-US/install.html

The [Dockerfile](./Dockerfile) was adapted from a Docker image
provided by the Rust team. It installs the following versions:

- rustup 1.17.0
- cargo 1.34.0
- rustc 1.34.0

## Learning Rust

A great way to get started learning Rust is by reading The Rust
Programming Language by Steve Klabnik and Carol Nichols. The book is
available [for free online](https://doc.rust-lang.org/book/), or in
[print](https://nostarch.com/rust).

## Building

Rust's official build tool is called Cargo. It will download
dependencies and call the Rust compiler as required. Dependencies are
configured in [Cargo.toml](./Cargo.toml).

Cargo needs to be called from the root of the bot (the folder with the
Cargo.toml file).

```sh
cargo build --release
```

## Running Tests

Rust has support for unit testing built into the language. Any
functions marked with the `#[test]` annotation are considered tests.

Tests can be run using Cargo:

```sh
cargo test
```

More information on how to write tests is available in
[The Rust Programming Language](https://doc.rust-lang.org/book/ch11-00-testing.html).

## Exporting the compiled executable

By default, Rust produces statically linked binaries, so you can just
copy out the executable file from the target directory and put it
wherever you want.

The name of the binary will match the name of the binary crate in
Cargo.toml.

Note: This binary has been built for the platform that it was compiled
on. In other words, if it was compiled on 64 bit Linux, you cannot
copy the binary to a Windows machine and run it. You WILL be able to
copy the binary between similar 64 bit Linux machines.

The machine that the compiled binary is run on does not need to have
the Rust toolchain installed.

```sh
cp ./target/release/<botFileName> <dest>
```

## Running

The compiled binary can be executed directly.

```sh
./target/release/<botFileName>
```

For convenience in development, you can compile and run through Cargo.

Note: This is not recommended for the tournament servers, since there
is a small runtime cost in Cargo checking that the compiled binary is
up to date before running it.

```sh
cargo run --release
```
