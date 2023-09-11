# si-ii-tp2-groupe6-svelte-22-23

## Lien de l'application
Voici le lien pour lancer l'application avec le frontend:

http://webapp.159.100.248.27.nip.io/

Lien de notre Swagger

http://app.159.100.248.27.nip.io/swagger/index.html

Lien de RabbitMQ:

- username: guest
- password: guest

http://rabbitmq.159.100.248.27.nip.io/

## Routes
Voici la liste des routes utilisées:

```
DRINKS

GET:      /drinks
POST:     /drink
BODY: 
{
name: string,
alcohol: float,
producer: string,
price: float,
country: string,
capacity: float
}
 
PUT:      /drink
BODY: 
{
id: int,
name: string,
alcohol: float,
producer: string,
price: float,
country: string,
capacity: float
}

DELETE    /drink:id


----------------------
RATING:

GET     /ratings/drink:id
POST    /rating/drink:id
BODY:
{
drinkId: int,
date: Date (string),
author: string,
title: string,
comment: string,
rating: int
}

```

# Découplage des micro-services

Nous avons décidé de découplé selon le modèle suivant:

- MQTT-Drinks
- MQTT-Ratings
- REST-Drinks
- REST-Ratings
