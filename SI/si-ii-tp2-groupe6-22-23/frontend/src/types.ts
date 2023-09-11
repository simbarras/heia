export type Beer = {
   alcohol: number,
   capacity: number,
   country: string,
   id?:	number,
   name: string,
   price: number,
   producer: string,
   avgRating?: number,
   cntRating?: number,
}

export type Rating = {
   id?: number,
   drinkId: number,
   date: string,
   author: string,
   title: string,
   comment: string,
   rating: number,
}