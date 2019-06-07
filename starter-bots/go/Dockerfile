
FROM entelectchallenge/base:2019

# gcc for cgo
RUN apt-get update && apt-get install -y --no-install-recommends \
		g++ \
		gcc \
		libc6-dev \
		make \
		pkg-config \
        wget \
        git \
	&& rm -rf /var/lib/apt/lists/*

ENV GOLANG_VERSION 1.12.5
ENV TENSORFLOW_VERSION 1.13.1

RUN set -eux; \
	goSha256='aea86e3c73495f205929cfebba0d63f1382c8ac59be081b6351681415f4063cf'; \
	url="https://golang.org/dl/go${GOLANG_VERSION}.linux-amd64.tar.gz"; \
	wget --progress=dot:binary -O go.tgz "$url"; \
	echo "${goSha256} *go.tgz" | sha256sum -c -; \
	tar -C /usr/local -xzf go.tgz; \
	rm go.tgz; \
    export PATH="/usr/local/go/bin:$PATH"; \
	go version; \
    wget --progress=dot:binary -O tensorflow.tgz "https://storage.googleapis.com/tensorflow/libtensorflow/libtensorflow-cpu-linux-x86_64-${TENSORFLOW_VERSION}.tar.gz" ; \
    tar -C /usr/local -xzf tensorflow.tgz; \
    rm tensorflow.tgz; \
    ldconfig;

ENV GOPATH /go
ENV PATH $GOPATH/bin:/usr/local/go/bin:$PATH

RUN mkdir -p "$GOPATH/src" "$GOPATH/bin" && chmod -R 777 "$GOPATH"
WORKDIR $GOPATH