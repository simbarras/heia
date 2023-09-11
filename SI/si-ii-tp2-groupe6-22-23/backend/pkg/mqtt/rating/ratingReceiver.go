package rating

import (
	"encoding/json"
	amqp "github.com/rabbitmq/amqp091-go"
	"log"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt"
)

type MqttRatingReceiver struct {
	mqtt.MqttReadWriter
	msgs             <-chan amqp.Delivery
	AddRatingAction  func(rating helper.Rating) (helper.Rating, error)
	GetRatingsAction func(drinkId uint32) ([]helper.Rating, error)
}

func NewMqttRatingReceiver() *MqttRatingReceiver {
	m := &MqttRatingReceiver{}
	m.Connect(queueRatingName, false)

	msg, err := m.Ch.Consume(
		m.Queue.Name, // queue
		"",           // consumer
		true,         // auto-ack
		false,        // exclusive
		false,        // no-local
		false,        // no-wait
		nil,          // args
	)
	m.msgs = msg
	mqtt.FailOnError(err, "Failed to register a consumer")

	return m
}

func (m *MqttRatingReceiver) Consume() {
	var forever chan struct{}

	go func() {
		for d := range m.msgs {
			// Json to Command
			var command helper.Command
			err := json.Unmarshal(d.Body, &command)
			if err != nil {
				continue
			}
			data, err := m.dispatch(command.Action, command.Data)
			if err != nil {
				continue
			}
			// Reply to sender
			m.Publish(d.ReplyTo, data, nil)
		}
	}()
	<-forever
}

func (m *MqttRatingReceiver) dispatch(action string, data []byte) ([]byte, error) {
	switch action {
	case "add":
		return m.add(data)
	case "getInfo":
		return m.getRatingsInfo(data)
	case "getAll":
		return m.getRatings(data)

	}
	return nil, nil
}

func (m *MqttRatingReceiver) add(data []byte) ([]byte, error) {
	// Get the rating from the request
	var rating helper.Rating
	err := json.Unmarshal(data, &rating)
	if err != nil {
		log.Println("Error unmarshalling rating from json:", err)
		return nil, err
	}

	// Call the action
	ratingAdded, err := m.AddRatingAction(rating)
	if err != nil {
		log.Println("Error adding rating:", err)
		return nil, err
	}

	// Set the response
	jsonRatingAdded, err := json.Marshal(ratingAdded)
	if err != nil {
		log.Println("Error marshalling rating to json:", err)
		return nil, err
	}
	return jsonRatingAdded, nil
}

func (m *MqttRatingReceiver) getRatingsInfo(data []byte) ([]byte, error) {
	// Get the drink id from the request
	var drinkId uint32
	err := json.Unmarshal(data, &drinkId)
	if err != nil {
		log.Println("Error unmarshalling drink id from json:", err)
		return nil, err
	}

	// Call the action
	ratings, err := m.GetRatingsAction(drinkId)
	if err != nil {
		log.Println("Error getting ratings:", err)
		return nil, err
	}
	count := len(ratings)
	var sum int32 = 0
	for _, rating := range ratings {
		sum += rating.Rating
	}
	average := float32(0)
	if sum != 0 {
		average = float32(sum) / float32(count)
	}
	log.Println("Average rating: ", average)
	log.Println("Count rating: ", count)

	// Set the response
	ratingInfo := helper.RatingInfo{
		Average: average,
		Count:   count,
	}
	jsonRatingInfo, err := json.Marshal(ratingInfo)
	if err != nil {
		log.Println("Error marshalling rating info to json:", err)
		return nil, err
	}
	return jsonRatingInfo, nil
}

func (m *MqttRatingReceiver) getRatings(data []byte) ([]byte, error) {
	// Get the drink id from the request
	var drinkId uint32
	err := json.Unmarshal(data, &drinkId)
	if err != nil {
		log.Println("Error unmarshalling drink id from json:", err)
		return nil, err
	}

	// Call the action
	ratings, err := m.GetRatingsAction(drinkId)
	if err != nil {
		log.Println("Error getting ratings:", err)
		return nil, err
	}

	// Set the response
	jsonRatings, err := json.Marshal(ratings)
	if err != nil {
		log.Println("Error marshalling ratings to json:", err)
		return nil, err
	}
	return jsonRatings, nil
}
