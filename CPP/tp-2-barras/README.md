# Géométrie et héritage

> Deadline : 25 13:00 Vendredi mars

## Objectives

- Utilisation des classes et l'héritage en C++
- Utilisations d'arrays et de la gestion de mémoire
- Listes d'initialisations
- Constructeurs/Déstructeurs
- I/O

## Spécifications

Nous voulons écrire un programme qui permet de créer du « ASCII art ». Pour ceci nous allons définir une série de
classes qui permettent de dessiner des formes géométriques.

Une classe _Point_ sera utilisé pour identifier un point dans l'espace. En se basant sur une classe Point développez,
une série de classes qui nous permettent de travailler avec des formes géométriques. Nous allons implémenter seulement 4
pour ce TP:

- Square
- Rectangle
- Circle
- Ellipse

Tous les paramètres et valeurs de retours des constructeurs et méthodes des classes de formes géométriques qui
représentent un point, doivent utiliser un objet _Point_ pour représenter cette valeur.

Les opérations suivantes doivent être possibles avec les différentes classes définies :

- Création d'un objet _Picture_ qui contient une liste d'objets à dessiner
- La liste des objets doit être un array au niveau de la classe
- La possibilité d'enregistrer le contenu d'un objet _Picture_ dans un fichier sous la forme :

```
 ##       ##
####      ##
 ##       ##
 
#########
```

Un point vide (espace) dans l'image est représenté avec un espace s'il est vide, et avec un # si une forme géométrique
le couvre.

Dans le constructeur de _Picture_, on peut définir la taille de l'image quand elle sera écrite dans un fichier.

Écrivez un programme qui génère une image aléatoire (formes (50+), positions et dimensions) d'une taille de 1000x1000 et
l'écrit dans un fichier.

## Lecture / Écriture

Écrivez un programme qui lit le fichier d'une image (comme vous avez pu créer avec la première application), et écrit un
fichier qui contient des statistiques sur votre image.

Nous voulons savoir les dimensions de l'image, le nombre de pixels noirs et le pourcentage de pixels noirs.

Écrivez ces informations sous la forme de texte dans le fichier de sortie utilisez le (format Markdown).

Le nom du fichier d'input (votre image en texte) et du fichier output (le texte avec les statistiques) doivent être
passé comme arguments à votre main.

## Exigences

>Chaque exigence pas respectée enlève de la note du TP 0.1.

- [x] Tous les constructeurs doivent utiliser les listes d'initialisation.
- [x] Vous devez utiliser l'héritage quand ceci est possible. Il est aussi possible d'ajouter des classes additionnelles
  pour compléter la hiérarchie de classes.
- [x] Toutes les classes implémentent le constructeur de copie.
- [x] Toutes les classes implémentent l'opérateur d'affectation.
- [x] Les attributs des classes sont accessibles seulement à eux même ou leurs sous classes
- [x] Votre repository ne doit pas contenir des fichiers binaires et temporaires (comme le repertoire cmake-build-debug
  ou les fichiers configs de l'IDE comme .idea).
- [x] Apres chaque commit, le code compile sur le CI de gitlab avec --Wall et --Werror comme options.
- [x] Les deux exécutables créés durant ce TP (création d'image et création des opérations pour recréer l'image) sont
  exécutées par le CI et les fichiers générés stockés comme artefacts.
- [x] Vous travaillez sur un fork du repo original (lien sur cyberlearn) et devez créer un merge request avant la
  deadline.

## Optionnel

- [ ] Créez un programme qui dessine quelque chose de plus intéressant que des formes aléatoires
- [ ] Créez un programme qui lit l'image généré et trouve les instructions nécessaires pour reproduire l'image. Essayez
  de minimiser le nombre d'instructions nécessaires.
    - [ ] Astuce : Commencez avec une version très simple qui utilise un Square par pixel noir dans l'image