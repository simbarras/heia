# si-ii-tp1-groupe6-spring-22-23

##   

## Lancer le projet

Pour lancer le projet, il faut lancer la commande suivante:

```bash
mvn spring-boot:run
```

## Lancer les tests

Pour lancer les tests, il faut lancer la commande suivante:

```bash
mvn test
```

## Déploiement sur Exoscale

Pour déployer le projet sur Exoscale, il suffit de pusher sur la branche `main` ou `develop` et d'attendre que le
workflow se termine.

Lorsque le workflow est terminé, il suffit de se connecter sur Exoscale et d'utiliser le docker-compose.yml qui se
trouve dans le dossier `/home/ubuntu/docker`.

Voici la commande docker-compose à utiliser:

```bash
docker compose -f docker-compose.yml up -d
```

Le docker compose va relancer la base de données (en gardant les données). Elle va aussi rechercher le container sur le
gitlab et le lancer.

## Endpoints

Voici la liste des endpoints disponibles pour l'application :

    API/V1 (01.11.22) TP SI
    
    (GET)   /games
    (POST)  /game/add
    
    body: {
    date: Date (string),
    location: string,
    homeTeam: string,
    awayTeam: string,
    leagueid: number
    }
    
    (GET)   /players
    (POST)  /player/add/
    
    body: {
    firstname: string,
    lastname: string,
    birthdate: Date (string),
    favoriteTeam: string
    }
    
    (GET)   /leagues
    (GET)   /league/add/:name
    (GET)   /league/delete/:leagueid
    
    (POST)  /league/add/player
    (POST)  /league/delete/player
    
    body: {
    leagueid: number,
    playerid: number
    }

## Base de données locale

Pour une base de données locale, voici la commande que nous avons utilisé:

```bash
docker run -p 3306:3306 --detach --name mysql --env="MYSQL_ROOT_PASSWORD=password" --env "MYSQL_DATABASE=db" mysql
```

Nous devons aussi modifier le fichier `application.properties` pour que le projet puisse se connecter à la base de données.