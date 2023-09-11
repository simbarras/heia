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
	port     = ":8082"
	basePath = "/api/v1"
)

var dbDrink *db.DBDrink

func main() {

	gin.SetMode(gin.ReleaseMode)

	dbDrink = db.DrinkDbConstructor()
	defer dbDrink.Close()

	router := gin.Default()
	router.GET(basePath+"/drinks", getDrinks)
	router.GET(basePath+"/drink/:id", getDrink)
	router.POST(basePath+"/drink", addDrink)
	router.DELETE(basePath+"/drink/:id", deleteDrink)
	router.PUT(basePath+"/drink/:id", updateDrink)

	router.Run(port)
	fmt.Println("Service rest drink started on port " + port)
}

func getDrinks(context *gin.Context) {
	drinks, err := dbDrink.GetAllDrinks()
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	context.IndentedJSON(http.StatusOK, drinks)
}

func getDrink(context *gin.Context) {
	id, err := strconv.ParseUint(context.Param("id"), 10, 32)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	drink, err := dbDrink.GetDrink(uint32(id))
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	context.IndentedJSON(http.StatusOK, drink)
}

func addDrink(context *gin.Context) {
	var drink helper.Drink
	context.BindJSON(&drink)
	addedDrink, err := dbDrink.AddDrink(drink)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	fmt.Println(addedDrink)
	context.IndentedJSON(http.StatusOK, addedDrink)
}

func deleteDrink(context *gin.Context) {
	id, err := strconv.ParseUint(context.Param("id"), 10, 32)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	err = dbDrink.DeleteDrink(uint32(id))
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	context.IndentedJSON(http.StatusOK, gin.H{"message": "Drink deleted"})
}

func updateDrink(context *gin.Context) {
	id, err := strconv.ParseUint(context.Param("id"), 10, 32)
	if err != nil {
		context.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	var drink helper.Drink
	err = context.BindJSON(&drink)
	if err != nil {
		return
	}
	drink.Id = uint32(id)
	err = dbDrink.UpdateDrink(drink)
	if err != nil {
		context.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	context.IndentedJSON(http.StatusOK, gin.H{"message": "Drink updated"})
}
