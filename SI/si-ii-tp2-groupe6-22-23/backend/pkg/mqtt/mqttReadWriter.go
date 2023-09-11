package mqtt

import (
	"context"
	"fmt"
	amqp "github.com/rabbitmq/amqp091-go"
	"log"
	"math/rand"
	"time"
)

const (
	url = "amqp://guest:guest@rabbitmq-svc" // On Excoscale
	//url  = "amqp://guest:guest@localhost" // On local
	port = ":5672"
)

type MqttReadWriter struct {
	Ch     *amqp.Channel
	conn   *amqp.Connection
	cancel context.CancelFunc
	ctx    context.Context
	Queue  amqp.Queue
}

func FailOnError(err error, msg string) {
	if err != nil {
		log.Panicf("%s: %s", msg, err)
	}
}

func (m *MqttReadWriter) Connect(queueName string, publisher bool) {
	fmt.Println("Try to connect MQTT")
	c, err := amqp.Dial(url + port)
	m.conn = c
	FailOnError(err, "Failed to connect to RabbitMQ")

	m.Ch, err = m.conn.Channel()
	FailOnError(err, "Failed to open a channel")

	m.Queue, err = m.Ch.QueueDeclare(
		queueName, // name
		false,     // durable
		false,     // delete when unused
		false,     // exclusive
		false,     // no-wait
		nil,       // arguments
	)
	FailOnError(err, "Failed to declare a queue")
	m.ctx, m.cancel = context.WithTimeout(context.Background(), 5*time.Second)
}

func (m *MqttReadWriter) Disconnect() {
	m.cancel()
	m.Ch.Close()
	m.conn.Close()
}

func (m *MqttReadWriter) Publish(queueName string, body []byte, callback chan []byte) {
	responseQueueName := ""
	if callback != nil {
		// Create a new queue to listen for the response
		log.Printf("Creating a new queue for the response")
		responseQueueName = m.Queue.Name + fmt.Sprintf("%d", rand.Int())
		responseQueue, err := m.Ch.QueueDeclare(
			responseQueueName,
			false, // durable
			true,  // delete when unused
			false, // exclusive
			false, // no-wait
			nil,   // arguments
		)
		FailOnError(err, "Failed to declare a queue")

		responseQueueConsumer, err := m.Ch.Consume(
			responseQueue.Name, // queue
			"",                 // consumer
			true,               // auto-ack
			false,              // exclusive
			false,              // no-local
			false,              // no-wait
			nil,                // args
		)
		FailOnError(err, "Failed to register a consumer")

		go func() {

			log.Printf("Listening for the response")
			d := <-responseQueueConsumer
			log.Printf("Received a message: %s", d.Body)
			callback <- d.Body
			// Unbind the queue
			m.Ch.QueueDelete(responseQueue.Name, false, false, false)
			log.Printf("Queue closed")

		}()
	}
	err := m.Ch.PublishWithContext(m.ctx,
		"",        // exchange
		queueName, // routing key
		false,     // mandatory
		false,     // immediate
		amqp.Publishing{
			ContentType: "Application/json",
			Body:        body,
			ReplyTo:     responseQueueName,
		})
	FailOnError(err, "Failed to publish a message")
	log.Printf("Message of length %d publish", len(body))
}
