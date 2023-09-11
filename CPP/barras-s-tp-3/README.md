# Closet Pairs

> **Deadline**: Vendredi, 22 avril 13:00

## Objectifs

- Implémentation de l’algorithme Closest Pairs
- Utilisation de tests unitaires
- Surcharge d'opérateurs

## Spécifications

Dans cet exercice, nous allons implémenter un algorithme vu au cours algorithmique 3, et l’appliquer sur un problème
d’analyse de données.

### Etape 1

Assurez-vous que votre TP compile. Ceci comporte les tests unitaires et l’outil starbucks.cpp à développer.

Pour ceci, faites une implémentation au moins fake de toutes les classes/fonctions nécessaires. Une fois que tout
compile, mettez en place la pipeline CI/CD pour qu’elle soit exécutée à chaque commit.

### Etape 2

Assurez-vous que tous les tests unitaires passent. Commencez avec les tests de Points, ensuite avec ClosestPair.
Complétez les tests unitaires des Points pour couvrir toutes les méthodes mises à disposition de Point.

Observez les performances de l’implémentation naïve et votre implémentation sur les 2 jeux de données (aléatoire et
worst case). Il existe un test unitaire qui compare leur performance et affiche des mesures à la console. Notez vos
observations dans le Readme du projet.

### Etape 3

Implémentez un outil qui s’appelle starbucks et qui prend en entrée le fichier tsv qui vous est fourni. Utilisez les
colonnes **Store Name** pour le nom, et **Longitude** et **Latitude** comme coordonnées X et Y.

Votre but : Réduisez le nombre de starbucks de 10% en combinant d’une manière itérative toujours les 2 starbucks les
plus proches.

Quand vous combinez 2 points, utilisez comme nom la concaténation des noms des 2 Starbucks que vous êtes en train de
combiner et comme nouvel emplacement du Starbucks les coordonnées X/Y entre les deux Starbucks en question (donc la
moyenne).

**Astuce** : Si vous êtes bloqué au point 2, vous pouvez déjà faire l’étape 3 avec l’implémentation naïve de ClosestPair
et la remplacer par la suite.

## Informations importantes

On peut trier un _std::vector_ avec:

```c++
std::sort(myVector.begin(), myVector().end(), [](const Point* a, const Point *b){...});
```

Pour créer un _std::pair_, on peut utiliser:

```c++
std::make_pair(...)
```

Des _std::set_ sont en effet des arbres. Ils sont donc triés et peuvent être traversé avec un itérateur. Si on
s’intéresse a une plage spécifique (par exemple toutes les valeurs entre x et y), on peut utiliser la méthode **
lower_bound** et **upper_bound** d’un _std::set_, qui vont retourner un itérateur vers le début et la fin des valeurs
qui respectent notre requête.

## Exigences

Chaque exigence pas respectée enlève 0.1 de la note du TP.

- [x] Vous devez lancer au moins une exception (et la tester) dans Point
    - La division par zéro se prête bien à ceci
- [x] Vous avez implémenté des tests unitaires pour tester toutes les fonctions de Point
- [x] Le code doit passer tous les tests sans erreurs
- [x] Le Readme.md contient vos observations niveau performance entre l’algorithme naïve et le vôtre
    - Il ne s’agit pas d’un rapport, mais des valeurs de vos mesures et quelques phrases pour l’interprétation des
      résultats.
- [x] Votre repository ne doit pas contenir des fichiers binaires et temporaires (comme le répertoire cmake-build-debug
  ou .idea)
- [x] Après chaque commit, le code compile sur le CI de gitlab avec -Wall et -Werror comme options et les tests
  unitaires sont exécutés
- [x] Vous travaillez sur un fork du repo original (lien sur cyberlearn) et devez créer un merge request avant la
  deadline.
    - Beat Wolf doit avoir accès à votre repository (ajouter comme membre ou mettre en public)

## Optionnel

- [ ] Gérez le problème des coordonnées polaires du dataset

## Observations

Voici les différentes observations que j'ai pu faire:

Avec les tests unitaires, nous voyons que l'algorithme est bien plus rapide que la solution naÏve. L'algorithme est
sensé être un O(LogN*N) (presque linéaire) tandis que la solution naïve est plutôt en O(n^2). On peut observer la
différence en comparant la différence de temps et la taille du dataset.

| # | Taille | Naive | ClosestPair |
| --- | --- | --- | --- |
| 1 | 200 | 142ms | 87ms |
| 2 | 400 | 470ms | 165ms |
| 3 | 800 | 1785ms | 271ms |

On voit que pour l'algo il y a une progression linéaire alors que pour la solution naive ça ressemble plutôt à une
courbe

## Annexe

L’algorithme à implémenter utilise la méthode de balayage. Dans cette méthode, chaque point doit seulement être comparé
à un sous ensemble des autres points pour trouver le voisin le plus proche. Ceci permet de passer d’un algorithme naïf
de O(n2) vers un algorithme de O(n log n).

L’idée générale de l’algorithme est de créer une structure de données dans laquelle tous les points sont triés selon un
certain axe (par exemple x) pour traverser les données par exemple de gauche à droite. Une deuxième structure de données
avec les mêmes points est créée, mais elle contient seulement les points qui sont des candidats d’être les points les
plus proches (voir Figure 1, zone de candidats). Cette collection est dynamique et triée selon l’autre axe (par exemple
y). Comme cette deuxième collection est dynamique (fréquent ajout et rétraction de points) une structure de donnée
appropriée comme un arbre est suggérée. En C++ un _std::set_ est un arbre trié.

> Plus de détails peuvent être trouvés dans le cours algorithmique 3.