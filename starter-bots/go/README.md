## Go Starter Bot

See the [Go homepage](https://golang.org/) for language installation support.

### Environment Setup

- Go 1.12.5
- libtensorflow 1.13.1

If your submission has dependencies, they should be vendored using using [Go modules](https://github.com/golang/go/wiki/Modules). Your
submission must have `go.mod` and `go.sum` files included, even if you do not use any dependencies. The `go.sum` file will
pin dependencies to a fixed version, to ensure reproducible builds.
 
Begin a project with the command `go mod init author/projectname` in an empty directory. A go program can then be written
as normal from `main.go`.

##### Optional: Tensorflow

The Go Dockerfile contains libtensorflow-1.13.1 configured, so you can use the official [Tensorflow bindings](https://www.tensorflow.org/install/lang_go) 
by simply adding the dependency to your `go.mod` file. If you wish to install them on your own environment, see
the above link for instructions. Note that the Go bindings work only on Linux and macOS X.


### Building 

Bots are built as follows
```
go build ./...      #to pull dependencies
go build .
```
A static-linked binary will be built in the same directory, which can be run with
```
./projectname       #Linux
projectname.exe     #Windows
```