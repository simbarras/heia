# Build
FROM node:16.11.0-alpine as build-webapp
WORKDIR /app
COPY package*.json ./

RUN npm install
COPY ./ .
RUN npm run build

# Deploy
FROM nginx:stable-alpine as production-stage
RUN mkdir /app
COPY --from=build-webapp /app/dist /app/dist
COPY nginx.conf /etc/nginx/nginx.conf

CMD ["nginx", "-g", "daemon off;"]
