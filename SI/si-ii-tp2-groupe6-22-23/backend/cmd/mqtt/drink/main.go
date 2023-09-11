package main

import (
	"si-ii-tp2-groupe6-22-23-backend/pkg/db"
	"si-ii-tp2-groupe6-22-23-backend/pkg/mqtt/drink"
)

func main() {
	// Connect to RabbitMQ
	drinkReceiver := drink.NewMqttDrinkReceiver()
	defer drinkReceiver.Disconnect()

	// Connect to DB
	dbDrink := db.DrinkDbConstructor()
	defer dbDrink.Close()

	//Register actions
	drinkReceiver.AddDrinkAction = dbDrink.AddDrink
	drinkReceiver.GetDrinksAction = dbDrink.GetAllDrinks
	drinkReceiver.GetDrinkAction = dbDrink.GetDrink
	drinkReceiver.DelDrinkAction = dbDrink.DeleteDrink
	drinkReceiver.PutDrinkAction = dbDrink.UpdateDrink

	//Start listening
	drinkReceiver.Consume()
}
