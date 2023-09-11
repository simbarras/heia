package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"si-ii-tp2-groupe6-22-23-backend/pkg/db"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"strconv"
)

const (
	port     = ":8081"
	basePath = "/api/v1"
)

var dbRating *db.DBRating

func main() {

	gin.SetMode(gin.ReleaseMode)

	dbRating = db.RatingDbConstructor()
	defer dbRating.Close()

	router := gin.Default()
	router.GET(basePath+"/ratings/:drinkId", getRatings)
	router.GET(basePath+"/ratings/info/:drinkId", getRatingInfo)
	router.POST(basePath+"/rating", addRating)

	router.Run(port)
	fmt.Println("Service rest rating started on port " + port)
}

func getRatings(context *gin.Context) {
	drinkId, err := strconv.ParseUint(context.Param("drinkId"), 10, 32)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	ratings, err := dbRating.GetRatings(uint32(drinkId))
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	context.IndentedJSON(200, ratings)
}

func getRatingInfo(context *gin.Context) {
	drinkId, err := strconv.ParseUint(context.Param("drinkId"), 10, 32)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	ratings, err := dbRating.GetRatings(uint32(drinkId))
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	var sum int32 = 0
	for _, rating := range ratings {
		sum += rating.Rating
	}
	average := float32(sum) / float32(len(ratings))
	fmt.Println(len(ratings))
	fmt.Println(average)
	context.IndentedJSON(200, helper.RatingInfo{Average: average, Count: len(ratings)})
}

func addRating(context *gin.Context) {
	var rating helper.Rating
	err := context.BindJSON(&rating)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	addedRating, err := dbRating.AddRating(rating)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	context.IndentedJSON(200, addedRating)
}
