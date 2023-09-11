FROM rikorose/gcc-cmake:gcc-11

WORKDIR /app

COPY . .

RUN mkdir -p /app/build && cd /app/build && cmake .. && make

EXPOSE 8080

CMD ["/app/build/ClusterServer"]
