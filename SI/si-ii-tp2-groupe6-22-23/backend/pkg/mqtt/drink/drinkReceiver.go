package drink

import (
	"encoding/json"
	amqp "github.com/rabbitmq/amqp091-go"
	"log"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt"
)

type MqttDrinkReceiver struct {
	mqtt.MqttReadWriter
	msgs            <-chan amqp.Delivery
	AddDrinkAction  func(drink helper.Drink) (helper.Drink, error)
	GetDrinkAction  func(uint322 uint32) (helper.Drink, error)
	GetDrinksAction func() ([]helper.Drink, error)
	PutDrinkAction  func(drink helper.Drink) error
	DelDrinkAction  func(uint322 uint32) error
}

func NewMqttDrinkReceiver() *MqttDrinkReceiver {
	m := &MqttDrinkReceiver{}
	m.Connect(queueDrinkName, false)

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

func (m *MqttDrinkReceiver) Consume() {
	var forever chan struct{}

	go func() {
		for d := range m.msgs {
			log.Printf("Received a message: %s", d.Body)
			// Json to Command
			var command helper.Command
			err := json.Unmarshal(d.Body, &command)
			if err != nil {
				log.Println("Error unmarshalling command from json:", err)
				continue
			}
			data, err := m.dispatch(command.Action, command.Data)
			if err != nil {
				log.Println("Error dispatching command:", err)
				continue
			}
			// Reply to sender
			m.Publish(d.ReplyTo, data, nil)
		}
	}()

	log.Printf(" [*] Waiting for messages. To exit press CTRL+C")
	<-forever
}

func (m *MqttDrinkReceiver) dispatch(action string, data []byte) ([]byte, error) {
	switch action {
	case "add":
		return m.addDrink(data)
	case "get":
		return m.getDrink(data)
	case "getAll":
		return m.getAllDrink()
	case "put":
		return m.updateDrink(data)
	case "del":
		return m.deleteDrink(data)
	}
	return nil, nil
}

func (m *MqttDrinkReceiver) addDrink(data []byte) ([]byte, error) {
	// Get the drink from the request
	var drink helper.Drink
	err := json.Unmarshal(data, &drink)
	if err != nil {
		log.Println("Error unmarshalling drink from json:", err)
		return nil, err
	}

	// Call the action
	drinkAdded, err := m.AddDrinkAction(drink)
	if err != nil {
		log.Println("Error adding drink:", err)
		return nil, err
	}

	// Set the response
	jsonDrinkAdded, err := json.Marshal(drinkAdded)
	if err != nil {
		log.Println("Error marshalling drink to json:", err)
		return nil, err
	}
	return jsonDrinkAdded, nil
}

func (m *MqttDrinkReceiver) getDrink(data []byte) ([]byte, error) {
	// Get the id from the request
	var id uint32
	err := json.Unmarshal(data, &id)
	if err != nil {
		log.Println("Error unmarshalling id from json:", err)
		return nil, err
	}

	// Call the action
	drink, err := m.GetDrinkAction(id)
	if err != nil {
		log.Println("Error getting drink:", err)
		return nil, err
	}

	// Set the response
	jsonDrink, err := json.Marshal(drink)
	if err != nil {
		log.Println("Error marshalling drink to json:", err)
		return nil, err
	}
	return jsonDrink, nil
}

func (m *MqttDrinkReceiver) getAllDrink() ([]byte, error) {
	// Call the action
	drinks, err := m.GetDrinksAction()
	if err != nil {
		log.Println("Error getting all drinks:", err)
		return nil, err
	}

	// Set the response
	jsonDrinks, err := json.Marshal(drinks)
	if err != nil {
		log.Println("Error marshalling drinks to json:", err)
		return nil, err
	}
	return jsonDrinks, nil
}

func (m *MqttDrinkReceiver) updateDrink(data []byte) ([]byte, error) {
	// Get the drink from the request
	var drink helper.Drink
	err := json.Unmarshal(data, &drink)
	if err != nil {
		log.Println("Error unmarshalling drink from json:", err)
		return nil, err
	}

	// Call the action
	err = m.PutDrinkAction(drink)
	if err != nil {
		log.Println("Error updating drink:", err)
		return nil, err
	}

	// Set the response
	return []byte("OK"), nil
}

func (m *MqttDrinkReceiver) deleteDrink(data []byte) ([]byte, error) {
	// Get the id from the request
	var id uint32
	err := json.Unmarshal(data, &id)
	if err != nil {
		log.Println("Error unmarshalling id from json:", err)
		return nil, err
	}

	// Call the action
	err = m.DelDrinkAction(id)
	if err != nil {
		log.Println("Error deleting drink:", err)
		return nil, err
	}

	// Set the response
	return []byte("OK"), nil
}
