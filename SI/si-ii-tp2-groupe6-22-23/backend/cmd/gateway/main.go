package main

import (
	"fmt"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"net/http"
	"si-ii-tp2-groupe6-22-23-backend/cmd/gateway/docs"
	_ "si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt/drink"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt/rating"
)

const (
	port       = ":8080"
	basePath   = "/api/v1"
	restDrink  = "http://drink-rest-svc"
	restRating = "http://rating-rest-svc"
)

var drinkSender *drink.MqttDrinkSender
var ratingSender *rating.MqttRatingSender

func main() {

	gin.SetMode(gin.ReleaseMode)

	// Initialisez le framework Gin
	router := gin.Default()
	docs.SwaggerInfo.BasePath = basePath

	// Accept all CORS requests
	router.Use(cors.New(cors.Config{
		AllowAllOrigins: true,
	}))

	// Initialisez le client MQTT
	drinkSender = drink.NewMqttDrinkSender()
	defer drinkSender.Disconnect()
	ratingSender = rating.NewMqttRatingSender()
	defer ratingSender.Disconnect()

	// Gérez les routes pour les boissons et checker si on passe par REST ou MQTT
	router.Any(basePath+"/drinks", getDrinks)
	router.GET(basePath+"/drink/:id", getDrink)
	router.POST(basePath+"/drink", addDrink)
	router.DELETE(basePath+"/drink/:id", deleteDrink)
	router.PUT(basePath+"/drink/:id", updateDrink)

	// Gérez les routes pour les notations
	router.GET(basePath+"/ratings/:drinkId", getRatings)
	router.GET(basePath+"/ratings/info/:drinkId", getRatingInfo)
	router.POST(basePath+"/rating", addRating)

	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	// Démarrez le serveur
	router.Run(port)
	fmt.Println("Service gateway started on port " + port)

}

func processTransfer(c *gin.Context, response *http.Response, err error) {
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	defer response.Body.Close()
	c.DataFromReader(response.StatusCode, response.ContentLength, response.Header.Get("Content-Type"), response.Body, nil)
}

// @Title Get all drinks
// @Version 1.0
// @Description get all drink available
// @Router /drinks [get]
// @Accept json
// @Produce json
// @Param mqtt query string false "Use MQTT for the request"
// @Success 200 {object} []helper.Drink
func getDrinks(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		drinkSender.GetDrinks(c)
	} else {
		response, err := http.Get(restDrink + c.Request.URL.Path)
		processTransfer(c, response, err)
	}
}

// @Title add a new drink
// @Version 1.0
// @Description Create a new drink
// @Router /drink [post]
// @Accept json
// @Produce json
// @Success 200 {object} helper.Drink
// @Param drink body helper.Drink true "Drink to add"
// @Param mqtt query string false "Use MQTT for the request"
func addDrink(c *gin.Context) {
	mqtt := c.DefaultQuery("mqtt", "true")
	if mqtt == "true" {
		drinkSender.AddDrink(c)
	} else {
		response, err := http.Post(restDrink+c.Request.URL.Path, "application/json", c.Request.Body)
		processTransfer(c, response, err)
	}
}

// @Title Update a drink
// @Version 1.0
// @Description Update a drink by id
// @Router /drink/{id} [put]
// @Accept json
// @Produce json
// @Success 200 {object} any
// @Param id path int true "Drink ID"
// @Param drink body helper.Drink true "Drink to update"
// @Param mqtt query string false "Use MQTT for the request"
func updateDrink(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		drinkSender.UpdateDrink(c)
	} else {
		req, err := http.NewRequest(http.MethodPut, restDrink+c.Request.URL.Path, c.Request.Body)

		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		req.Header.Set("Content-Type", "application/json")
		response, err := http.DefaultClient.Do(req)
		processTransfer(c, response, err)
	}
}

// @Title Delete a drink
// @Version 1.0
// @Description Delete a drink by id
// @Router /drink/{id} [delete]
// @Accept json
// @Produce json
// @Success 200 {object} any
// @Param id path int true "Drink ID"
// @Param mqtt query string false "Use MQTT for the request"
func deleteDrink(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		drinkSender.DeleteDrink(c)
	} else {
		req, err := http.NewRequest(http.MethodDelete, restDrink+c.Request.URL.Path, nil)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		response, err := http.DefaultClient.Do(req)
		processTransfer(c, response, err)
	}
}

// @Title Get one drink
// @Version 1.0
// @Description get a drink by id
// @Router /drink/{id} [get]
// @Accept json
// @Produce json
// @Success 200 {object} helper.Drink
// @Param id path int true "Drink ID"
// @Param mqtt query string false "Use MQTT for the request"
func getDrink(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		drinkSender.GetDrink(c)
	} else {
		fmt.Println(c.Request.URL.Path)
		response, err := http.Get(restDrink + c.Request.URL.Path)
		processTransfer(c, response, err)
	}

}

// @Title Rating by drink id
// @Version 1.0
// @Description get all ratings for a drink
// @Router /ratings/{id} [get]
// @Accept json
// @Produce json
// @Success 200 {object} []helper.Rating
// @param id path int true "Drink id"
// @Param mqtt query string false "Use MQTT for the request"
func getRatings(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		ratingSender.GetRatings(c)
	} else {
		response, err := http.Get(restRating + c.Request.URL.Path)
		processTransfer(c, response, err)
	}
}

// @Title Metadata for a drink
// @Version 1.0
// @Description Get number and average ratings for a drink
// @Router /ratings/info/{id} [get]
// @Accept json
// @Produce json
// @Success 200 {object} helper.RatingInfo
// @param id path int true "Drink id"
// @Param mqtt query string false "Use MQTT for the request"
func getRatingInfo(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		ratingSender.GetRatingsInfo(c)
	} else {
		response, err := http.Get(restRating + c.Request.URL.Path)
		processTransfer(c, response, err)
	}
}

// @Title Add a rating
// @Version 1.0
// @Description add a rating for a drink
// @Router /rating [post]
// @Accept json
// @Produce json
// @Success 200 {object} helper.Rating
// @param rating body helper.Rating true "Rating to add"
// @Param mqtt query string false "Use MQTT for the request"
func addRating(c *gin.Context) {
	if mqtt := c.DefaultQuery("mqtt", "false"); mqtt == "true" {
		fmt.Println("USE MQTT")
		ratingSender.AddRating(c)
	} else {
		response, err := http.Post(restRating+c.Request.URL.Path, "application/json", c.Request.Body)
		processTransfer(c, response, err)
	}
}
