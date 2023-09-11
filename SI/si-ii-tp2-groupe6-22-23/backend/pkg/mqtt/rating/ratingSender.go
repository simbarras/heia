package rating

import (
	"encoding/json"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt"
)

type MqttRatingSender struct {
	mqtt.MqttReadWriter
}

const (
	queueRatingName = "rating"
)

func NewMqttRatingSender() *MqttRatingSender {
	m := &MqttRatingSender{}
	m.Connect(queueRatingName, true)
	return m
}

func manageError(c *gin.Context, method string, err error) {
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		log.Fatal("Error at " + method + ": " + err.Error())
	}
}

func (m *MqttRatingSender) GetRatings(context *gin.Context) {
	// Extract the drink id from the request
	drinkId := context.Param("drinkId")

	// Make a command object
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "getAll",
		Data:   []byte(drinkId),
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending rating to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	var ratings []helper.Rating
	err = json.Unmarshal(body, &ratings)
	manageError(context, "Json unmarshal ratings", err)

	// Send the response to the client
	context.JSON(http.StatusOK, ratings)
}

func (m *MqttRatingSender) GetRatingsInfo(context *gin.Context) {
	// Extract the drink id from the request
	drinkId := context.Param("drinkId")

	// Make a command object
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "getInfo",
		Data:   []byte(drinkId),
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending rating to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	var ratingInfo helper.RatingInfo
	err = json.Unmarshal(body, &ratingInfo)
	manageError(context, "Json unmarshal rating info", err)

	// Send the response to the client
	context.JSON(http.StatusOK, ratingInfo)
}

func (m *MqttRatingSender) AddRating(context *gin.Context) {
	// Extract the rating from the request body
	var rating helper.Rating
	err := context.BindJSON(&rating)
	manageError(context, "Json unmarshal rating", err)

	// Add rating to a command object
	jsonRating, err := json.Marshal(rating)
	manageError(context, "Json marshal rating", err)
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "add",
		Data:   jsonRating,
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending rating to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	var newRating helper.Rating
	err = json.Unmarshal(body, &newRating)
	manageError(context, "Json unmarshal rating", err)

	// Send the response to the client
	context.JSON(http.StatusOK, newRating)
}
