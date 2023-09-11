package main

import (
	"si-ii-tp2-groupe6-22-23-backend/pkg/db"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt/rating"
)

func main() {
	// Connect to RabbitMQ
	ratingReceiver := rating.NewMqttRatingReceiver()
	defer ratingReceiver.Disconnect()

	// Connect to DB
	dbRating := db.RatingDbConstructor()
	defer dbRating.Close()

	//Register actions
	ratingReceiver.AddRatingAction = dbRating.AddRating
	ratingReceiver.GetRatingsAction = dbRating.GetRatings

	//Start listening
	ratingReceiver.Consume()
}
