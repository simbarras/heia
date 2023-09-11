package db

import (
	"context"
	"crypto/tls"
	"fmt"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"go.mongodb.org/mongo-driver/mongo/readpref"
)

const (
	uri              = "mongodb+srv://tp02-orm:OukxMq4zow9HvENb@tp02.etfbme8.mongodb.net/?retryWrites=true&w=majority"
	dbname           = "si-ii-backend"
	drinkCollection  = "drinks"
	ratingCollection = "ratings"
)

type DBReadWriter struct {
	Client *mongo.Client
	Ctx    context.Context
	cancel context.CancelFunc
}

// Close This is a user defined method to close resources.
// This method closes mongoDB connection and cancel context.
func (db *DBReadWriter) Close() {

	// CancelFunc to cancel to context
	defer db.cancel()

	// client provides a method to close
	// a mongoDB connection.
	defer func() {

		// client.Disconnect method also has deadline.
		// returns error if any,
		if err := db.Client.Disconnect(db.Ctx); err != nil {
			panic(err)
		}
		fmt.Println("connection to MongoDB closed.")
	}()
}

// This is a user defined method that returns mongo.Client,
// context.Context, context.CancelFunc and error.
// mongo.Client will be used for further database operation.
// context.Context will be used set deadlines for process.
// context.CancelFunc will be used to cancel context and
// resource associated with it.

func (db *DBReadWriter) Connect(uri string) error {

	// ctx will be used to set deadline for process, here
	// deadline will of 30 seconds.
	db.Ctx, db.cancel = context.WithCancel(context.Background())

	// mongo.Connect return mongo.Client method without certificate validation
	clientOptions := options.Client().ApplyURI(uri)
	clientOptions.SetTLSConfig(&tls.Config{InsecureSkipVerify: true})
	c, err := mongo.Connect(db.Ctx, clientOptions)
	db.Client = c
	fmt.Println("connected to mongoDB")
	defer db.ping()
	return err
}

// This is a user defined method that accepts
// mongo.Client and context.Context
// This method used to ping the mongoDB, return error if any.
func (db *DBReadWriter) ping() error {

	// mongo.Client has Ping to ping mongoDB, deadline of
	// the Ping method will be determined by cxt
	// Ping method return error if any occurred, then
	// the error can be handled.
	if err := db.Client.Ping(db.Ctx, readpref.Primary()); err != nil {
		return err
	}
	fmt.Println("connection OK")
	return nil
}
