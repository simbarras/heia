package main

import (
	"github.com/gin-gonic/gin"
	"simbarras.ch/fibonacci"
)

const (
	StatusOk         = 200
	StatusBadRequest = 400
	Route            = "/api/v1/fibonacci/:i"
	Host             = ":"
	Port             = "8080"
	AsciiOffset      = 48
)

func main() {
	gin.SetMode(gin.ReleaseMode)
	router := gin.Default()
	router.GET(Route, getFibonacci)
	router.Run(Host + Port)
}

func getFibonacci(c *gin.Context) {
	i := fibonacci.S2i(c.Param("i"))
	if i < 0 {
		c.JSON(StatusBadRequest, "i must be a number greater than 0")
		return
	}
	var fib, _ = fibonacci.Fast_Fibonacci(i)
	var res = fibonacci.FibonacciResponse{i, fib}
	c.JSON(StatusOk, res)
}
