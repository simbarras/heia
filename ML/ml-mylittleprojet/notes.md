# 1. Présentation de l'approche utilisée

Pour ce projet, nous partons d'un dataset de production solaire contenant 15 champs utiles afin de faire des prédictions
sur la production solaire à un instant T.

Nous avons choisi de faire une approche de type "regression" afin de prédire cette production solaire.

La démarche est la suivante:

1. Exploration des données
2. Choix des champs à utiliser
3. Nettoyage des données et préprocessing
4. Choix du modèle et comparaison des résultats
5. Choix du modèle final et prédictions
6. Postprocessing des résultats
7. Conclusion

# 2. Exploration des données

## 2.1. Import des librairies

## 2.2. Import des données

## 2.3 Exploration et visualisation

# 3. Choix des champs à utiliser

Pour choisir les champs utiles, nous avons fait une analyse avec SelectKBest afin de voir les champs les plus pertinents.
Nous avons aussi ajouté l'heure car nous trouvons que c'est un champ important pour la prédiction. L'index a été supprimé car non pertinent.



# 4. Nettoyage des données et preprocessing

# 5. Choix du modèle et comparaison des résultats

Nous avons choisi de faire une recherche aléatoire sur les hyperparamètres afin de trouver le meilleur modèle.

Les estimateurs utilisés sont les suivants:
- SVR
- RandomForestRegressor
- GradientBoostingRegressor
- KNeighborsRegressor

Nous avons aussi testé d'autre estimateurs mais, soit le temps pour l'entraînement était __TRÈS__ long, soit les résultats étaient mauvais :
- MLPRegressor
- AdaBoostRegressor

# 6. Choix du modèle final et prédictions
Au vu des résultats ci-dessus, on voit que la RandomForest obtient un R2 score bien meilleur ainsi qu'un MSE plus bas que les autres.

Le subset de test n'est pas pris aléatoirement dans le dataset mais ce sont le 20 premiers pourcents de celui-ci. Aucune de ces lignes n'a été utilisée pour l'entraînement. Au début, notre erreur a été de prendre le subset de manière aléatoire mais les résultats montraient que les estimateurs étaient très overfittés et que lors de la prédiction finale, les résultats étaient très mauvais (0.16 avec le R2 score).

Maintenant que l'estimateur est connu, il est temps de tuner les hyperparamètres afin d'obtenir le meilleur modèle possible. Nous avons donc fait une recherche aléatoire sur les hyperparamètres afin de trouver le meilleur modèle qui optimise le R2 score et minimise le MSE.
Il faudra aussi appliquer un postprocessing afin d'améliorer les résultats mais ce sera dans un second temps.


# 7. Postprocessing des résultats

# 8. Conclusion