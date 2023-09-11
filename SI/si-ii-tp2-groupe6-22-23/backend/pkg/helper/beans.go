package helper

type DBEntity struct {
	Drink
	Rating
}

type Drink struct {
	Id       uint32  `bson:"_id" json:"id"`
	Name     string  `bson:"name" json:"name"`
	Alcohol  float32 `bson:"alcohol" json:"alcohol"`
	Producer string  `bson:"producer" json:"producer"`
	Price    float32 `bson:"price" json:"price"`
	Country  string  `bson:"country" json:"country"`
	Capacity float32 `bson:"capacity" json:"capacity"`
}

type RatingInfo struct {
	Average float32 `json:"average"`
	Count   int     `json:"count"`
}

type Rating struct {
	Id      uint32 `bson:"_id" json:"id"`
	DrinkId uint32 `bson:"drinkId" json:"drinkId"`
	Date    string `bson:"date" json:"date"`
	Author  string `bson:"author" json:"author"`
	Title   string `bson:"title" json:"title"`
	Comment string `bson:"comment" json:"comment"`
	Rating  int32  `bson:"rating" json:"rating"`
}

type Command struct {
	Action string `json:"action"`
	Data   []byte `json:"data"`
}
