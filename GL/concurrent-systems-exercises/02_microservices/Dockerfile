FROM node:19 as builder
COPY . .
RUN npm install --save-dev --force
RUN npm run build

FROM alpine as template
RUN apk update && apk add --update nodejs
COPY --from=builder ./.env ./.env
EXPOSE 3000
ENTRYPOINT [ "node", "app.js" ]


FROM template as graphql-svc
COPY --from=builder ./build/graphql-svc/app.js /app.js

FROM template as grpc-svc
RUN apk add ca-certificates wget \
    && update-ca-certificates \
    && wget -qO/bin/grpc_health_probe https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/v0.4.13/grpc_health_probe-linux-amd64  \
    && chmod +x /bin/grpc_health_probe
COPY --from=builder ./build/grpc-svc/app.js /app.js
COPY --from=builder ./build/grpc-svc/schema.proto ./schema.proto
COPY --from=builder ./build/grpc-svc/health.proto ./health.proto

