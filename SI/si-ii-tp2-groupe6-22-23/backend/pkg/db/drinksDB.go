package db

import (
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"log"
	"math/rand"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
)

type DBDrink struct {
	DBReadWriter
	collection *mongo.Collection
}

func (db *DBDrink) GetDrink(id uint32) (helper.Drink, error) {
	var drink helper.Drink
	err := db.collection.FindOne(db.Ctx, bson.M{"_id": id}).Decode(&drink)
	if err != nil {
		return drink, err
	}
	return drink, nil
}

func (db *DBDrink) GetAllDrinks() ([]helper.Drink, error) {
	var drinks = make([]helper.Drink, 0)
	cursor, err := db.collection.Find(db.Ctx, bson.D{{}})
	if err != nil {
		return drinks, err
	}
	if err = cursor.All(db.Ctx, &drinks); err != nil {
		return drinks, err
	}
	return drinks, nil
}

func (db *DBDrink) DeleteDrink(id uint32) error {
	_, err := db.collection.DeleteOne(db.Ctx, bson.M{"_id": id})
	if err != nil {
		return err
	}
	return nil
}

func (db *DBDrink) UpdateDrink(drink helper.Drink) error {
	_, err := db.collection.UpdateOne(db.Ctx, bson.M{"_id": drink.Id}, bson.D{{"$set", bson.D{
		{"name", drink.Name},
		{"alcohol", drink.Alcohol},
		{"producer", drink.Producer},
		{"price", drink.Price},
		{"country", drink.Country},
		{"capacity", drink.Capacity},
	}}})
	if err != nil {
		return err
	}
	return nil
}

func (db *DBDrink) AddDrink(drink helper.Drink) (helper.Drink, error) {
	//Generate uuid for drink
	drink.Id = freeDrinkId(db.GetDrink)
	_, err := db.collection.InsertOne(db.Ctx, drink)
	if err != nil {
		return drink, err
	}
	log.Printf("Drink with id %d added to database", drink.Id)
	return drink, nil
}

func DrinkDbConstructor() *DBDrink {
	db := DBDrink{}
	err := db.Connect(uri)
	if err != nil {
		return nil
	}
	db.collection = db.Client.Database(dbname).Collection(drinkCollection)
	return &db
}

func freeDrinkId(checker func(uint32) (helper.Drink, error)) uint32 {
	var id uint32
	for {
		id = rand.Uint32()
		_, err := checker(id)
		if err != nil {
			break
		}
	}
	return id
}
