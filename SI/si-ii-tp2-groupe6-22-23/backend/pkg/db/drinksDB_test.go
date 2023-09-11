package db

import (
	"fmt"
	"si-ii-tp2-groupe6-22-23-backend/pkg/helper"
	"testing"
)

func initDb() *DBDrink {
	db := DrinkDbConstructor()
	return db
}

func TestDBDrink_AddRemoveDrink(t *testing.T) {
	db := initDb()
	defer db.Close()

	drink := helper.Drink{
		Id:       0,
		Name:     "test",
		Alcohol:  1.0,
		Producer: "test",
		Price:    1.0,
		Country:  "test",
		Capacity: 1.0,
	}

	drinkAdded, err := db.AddDrink(drink)
	if err != nil {
		t.Error(err)
	}
	id := drinkAdded.Id
	drink.Id = id
	fmt.Println(db.GetAllDrinks())
	res, err := db.GetDrink(id)
	if err != nil {
		t.Error(err)
	}
	fmt.Println(res)
	if res != drink {
		t.Error("Drink not added")
	}
	fmt.Println("Drink added")

	drink.Capacity = 2.0
	err = db.UpdateDrink(drink)
	if err != nil {
		t.Error(err)
	}
	res, err = db.GetDrink(id)
	if err != nil {
		t.Error(err)
	}
	if res != drink {
		t.Error("Drink not updated")
	}
	fmt.Println("Drink updated")

	err = db.DeleteDrink(id)
	if err != nil {
		t.Error(err)
	}
	res, err = db.GetDrink(id)
	if err == nil {
		t.Error("Drink not deleted")
	}
	fmt.Println("Drink deleted")
}
