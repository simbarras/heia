############################
# STEP 1 build executable binary
############################
FROM golang:alpine AS builder
# Install git.
# Git is required for fetching the dependencies.
RUN apk update && apk add --no-cache git build-base
WORKDIR $GOPATH/src/mypackage/myapp/
COPY backend .
# Build the binary.
RUN CGO_ENABLED=0 GOARCH=amd64 GOOS=linux GIN_MODE=release go build -ldflags="-w -s" -o /go/bin/gateway cmd/gateway/main.go
RUN CGO_ENABLED=0 GOARCH=amd64 GOOS=linux GIN_MODE=release go build -ldflags="-w -s" -o /go/bin/drink-rest cmd/rest/drink/main.go
RUN CGO_ENABLED=0 GOARCH=amd64 GOOS=linux GIN_MODE=release go build -ldflags="-w -s" -o /go/bin/rating-rest cmd/rest/rating/main.go
RUN CGO_ENABLED=0 GOARCH=amd64 GOOS=linux GIN_MODE=release go build -ldflags="-w -s" -o /go/bin/drink-mqtt cmd/mqtt/drink/main.go
RUN CGO_ENABLED=0 GOARCH=amd64 GOOS=linux GIN_MODE=release go build -ldflags="-w -s" -o /go/bin/rating-mqtt cmd/mqtt/rating/main.go

############################
# STEP 2 build a small image
############################

FROM scratch as gateway
EXPOSE 8080
# Copy our static executable.
COPY --from=builder /go/bin/gateway /go/bin/app
# Run the fibonacci binary.
ENTRYPOINT ["/go/bin/app"]

FROM scratch as drink-rest
EXPOSE 8080
# Copy our static executable.
COPY --from=builder /go/bin/drink-rest /go/bin/app
# Run the fibonacci binary.
ENTRYPOINT ["/go/bin/app"]

FROM scratch as rating-rest
EXPOSE 8080
# Copy our static executable.
COPY --from=builder /go/bin/rating-rest /go/bin/app
# Run the fibonacci binary.
ENTRYPOINT ["/go/bin/app"]

FROM scratch as drink-mqtt
# Copy our static executable.
COPY --from=builder /go/bin/drink-mqtt /go/bin/app
# Run the fibonacci binary.
ENTRYPOINT ["/go/bin/app"]

FROM scratch as rating-mqtt
# Copy our static executable.
COPY --from=builder /go/bin/rating-mqtt /go/bin/app
# Run the fibonacci binary.
ENTRYPOINT ["/go/bin/app"]