# TP6 - Clustering de points

> Deadline: Vendredi 17 juin 13:00

## Objectifs

- Utilisation d’une librairie externe (header only)
- Utilisation de la STD
- Développement/Utilisation d’un serveur web
- Implémentation de 2 algorithmes classiques de clustering

## Spécifications

Dans ce dernier TP nous allons implémenter 2 algorithmes de clustering, avec une visualisation dans une page web. Comme
il est difficile de tester les algorithmes de clustering, l’interface web permet de vérifier visuellement si les
algorithmes fonctionnent.

Vos taches sont de compléter les 2 algorithmes et le serveur web.

Vous avez besoin de la classe **Point** et **ClosestPair** des TP précédents pour ce TP. Complétez-les si nécessaire.
Commencez par implémenter ce qui est nécessaire pour que le projet compile correctement.

En suite complétez l’interface web pour faciliter de développement des 2 algorithmes.

### Serveur Web

Pour l’interface web, vous avez reçu un frontend en Vue.js et vous devez compléter le backend REST écrit en C++. La
classe server.cpp implémente partiellement le serveur REST nécessaire pour faire fonctionner la partie Vue.js.

Ils manquent :

- Un endpoint /author qui retourne votre nom
- Les fichiers statiques (tout ce qui se trouve dans le répertoire web)
- Le lancement du serveur

Téléchargez le fichier header qui contient la librairie
depuis : [https://github.com/yhirose/cpp-httplib](https://github.com/yhirose/cpp-httplib). Suivez la documentation pour
implémenter ce que vous avez besoin.

**Astuce** : Si vous avez des problèmes pour faire fonctionner, n’oubliez pas la fonction F12 de votre navigateur pour
voir la communication avec le serveur.

### kMeans

Implémentez **initKMeans**, **closestCluster** et **kmeans** dans le fichier **Clustering.cpp**. La classe Cluster
devrait grandement vous aider pour l’implémentation de cet algorithme.

### Hierarchical

Implémentez **initHierchical** et **hierarchical** dans le fichier **Clustering.cpp**. A noter, utilisez le ClosestPair
pour déterminer quels Clusters il faut mettre ensemble à chaque étape.

## Exigences

> Chaque exigence pas respectée enlève 0.1 de la note du TP.

- [x] Serveur web implémenté est fonctionnel
- [x] Les tests unitaires passent
- [x] ClosestPair utilisé pour implémenter le clustering hiérarchique
- [x] Votre repository ne doit pas contenir des fichiers binaires et temporaires (comme le répertoire cmake-build-debug
  ou .idea)
- [x] Après chaque commit, le code compile sur le CI de gitlab avec -Wall et -Werror comme options et les tests
  unitaires sont exécutés
- [x] Vous travaillez sur un fork du repo original (lien sur cyberlearn) et devez créer un merge request avant la
  deadline.
    - Beat Wolf doit avoir accès à votre repository (ajouter comme membre ou mettre en public)

## Annexe

Le KMeans clustering fonctionne avec l’idée suivante : K aléatoires points sont choisis comme centres de cluster durant
l’initialisation. Ensuite, on met chaque point dans le cluster le plus proche et on met à jour le centre du cluster. On
s’arrête quand l’algorithme a convergé, c’est-à-dire que durant une itération aucun point n’a changé de cluster.

Le clustering hiérarchique a une autre stratégie. Au début chaque point est un cluster. Ensuite on réunit toujours les 2
clusters les plus proches et on s’arrête quand ils restent plus que le nombre de clusters souhaités.

