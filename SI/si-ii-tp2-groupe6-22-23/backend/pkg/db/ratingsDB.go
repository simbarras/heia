package db

import (
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"math/rand"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"time"
)

type DBRating struct {
	DBReadWriter
	collection *mongo.Collection
}

func RatingDbConstructor() *DBRating {
	db := DBRating{}
	err := db.Connect(uri)
	if err != nil {
		return nil
	}
	db.collection = db.Client.Database(dbname).Collection(ratingCollection)
	return &db
}

func (db *DBRating) GetRatings(drinkId uint32) ([]helper.Rating, error) {
	var ratings []helper.Rating
	cursor, err := db.collection.Find(db.Ctx, bson.D{{"drinkId", drinkId}})
	if err != nil {
		return ratings, err
	}
	if err = cursor.All(db.Ctx, &ratings); err != nil {
		return ratings, err
	}
	return ratings, nil
}

func (db *DBRating) GetRating(id uint32) (helper.Rating, error) {
	var rating helper.Rating
	err := db.collection.FindOne(db.Ctx, bson.M{"_id": id}).Decode(&rating)
	if err != nil {
		return rating, err
	}
	return rating, nil
}

func (db *DBRating) AddRating(rating helper.Rating) (helper.Rating, error) {
	rating.Id = freeRatingId(db.GetRating)
	rating.Date = time.Now().UTC().String()
	_, err := db.collection.InsertOne(db.Ctx, rating)
	if err != nil {
		return rating, err
	}
	return rating, nil
}

func freeRatingId(checker func(uint32) (helper.Rating, error)) uint32 {
	var id uint32
	rand.Seed(time.Now().UTC().UnixNano())
	for {
		id = rand.Uint32()
		_, err := checker(id)
		if err != nil {
			break
		}
	}
	return id
}
