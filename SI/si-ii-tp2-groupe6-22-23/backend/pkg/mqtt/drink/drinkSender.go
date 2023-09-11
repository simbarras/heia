package drink

import (
	"encoding/json"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt"
	"strconv"
)

type MqttDrinkSender struct {
	mqtt.MqttReadWriter
}

const (
	queueDrinkName = "drink"
)

func NewMqttDrinkSender() *MqttDrinkSender {
	m := &MqttDrinkSender{}
	m.Connect(queueDrinkName, true)
	return m
}

func manageError(c *gin.Context, method string, err error) {
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		log.Fatal("Error at " + method + ": " + err.Error())
	}
}

func (m *MqttDrinkSender) GetDrinks(context *gin.Context) {
	// Make a command object
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "getAll",
		Data:   nil,
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending drink to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	var drinks []helper.Drink
	err = json.Unmarshal(body, &drinks)
	manageError(context, "Json unmarshal drinks", err)

	// Send the response to the client
	context.JSON(http.StatusOK, drinks)
}

func (m *MqttDrinkSender) GetDrink(context *gin.Context) {
	// Read the id from the request
	id := context.Param("id")

	// Add id to a command object
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "get",
		Data:   []byte(id),
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending drink to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	var drink helper.Drink
	err = json.Unmarshal(body, &drink)
	manageError(context, "Json unmarshal drink", err)

	// Send the response to the client
	context.JSON(http.StatusOK, drink)
}

func (m *MqttDrinkSender) AddDrink(context *gin.Context) {
	// Get the drink from the request
	var drink *helper.Drink
	err := context.BindJSON(&drink)
	manageError(context, "Context bind", err)

	// Add drink to a command object
	jsonDrink, err := json.Marshal(drink)
	manageError(context, "Json marshal drink", err)
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "add",
		Data:   jsonDrink,
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending drink to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	var addedDrink helper.Drink
	err = json.Unmarshal(body, &addedDrink)
	manageError(context, "Json unmarshal drink", err)

	// Send the response to the client
	context.JSON(http.StatusOK, addedDrink)
}

func (m *MqttDrinkSender) UpdateDrink(context *gin.Context) {
	// Get the drink and the id from the request
	var drink *helper.Drink
	err := context.BindJSON(&drink)
	manageError(context, "Context bind", err)
	id, err := strconv.ParseUint(context.Param("id"), 10, 32)
	manageError(context, "ParseUint", err)
	drink.Id = uint32(id)

	// Add drink to a command object
	jsonDrink, err := json.Marshal(drink)
	manageError(context, "Json marshal drink", err)
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "put",
		Data:   jsonDrink,
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending drink to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	result := string(body)
	// Send the response to the client
	context.JSON(http.StatusOK, result)
}

func (m *MqttDrinkSender) DeleteDrink(context *gin.Context) {
	// Read the id from the request
	id := context.Param("id")

	// Add id to a command object
	jsonCommand, err := json.Marshal(helper.Command{
		Action: "del",
		Data:   []byte(id),
	})
	manageError(context, "Json marshal command", err)

	// Send the command to the queue
	log.Println("Sending drink to queue")
	response := make(chan []byte)
	m.Publish(m.Queue.Name, jsonCommand, response)

	// Wait for the response
	body := <-response
	result := string(body)
	// Send the response to the client
	context.JSON(http.StatusOK, result)
}
