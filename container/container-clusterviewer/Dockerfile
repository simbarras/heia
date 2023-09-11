############################
# STEP 1 build executable binary
############################
FROM golang:alpine AS builder
# Install git.
# Git is required for fetching the dependencies.
RUN apk update && apk add --no-cache git build-base
WORKDIR $GOPATH/src/mypackage/myapp/
COPY . .
# Build the binary.
RUN CGO_ENABLED=0 GOARCH=amd64 GOOS=linux go build -ldflags="-w -s" -o /go/bin/container-clusterviewer cmd/main.go

ENTRYPOINT ["/go/bin/container-clusterviewer"]


############################
# STEP 2 build a small image
############################

# FROM scratch as clusterviewer
# Copy our static executable.
# COPY --from=builder /go/bin/container-clusterviewer /go/bin/app
# COPY --from=builder /go/src/mypackage/myapp/kubeconfig /go/bin/app
# Run the binary
# ENTRYPOINT ["/go/bin/app"]

