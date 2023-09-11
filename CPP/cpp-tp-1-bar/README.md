# TP1 - Calculatrice en ligne de commande

> Deadline : Vendredi 4 mars 13:00

## Objectifs

- L’utilisation basique de C++
- Entrées sorties C++
- Utilisation de Git, Gitlab et du CI de Gitlab

## Spécifications

Nous allons développer une simple calculatrice en ligne de commande qui fonctionne de la manière suivante :

1. L’outil demande l’utilisateur pour un chiffre a (virgule flottante)
2. L’outil demande l’utilisateur pour un opérateur (+ - * / )
3. L’outil demande l’utilisateur pour un deuxième chiffre b (virgule flottante)
4. Le résultat du calcul (a op b) est affiché
5. On retourne au point 2 (avec le chiffre a venant du résultat du dernier calcul)

Pour terminer l’application, l’utilisateur peut entrer l'opérateur # (à la place d’un des opérateurs standards).

Voici un exemple d’interaction : 1.9 + 2.1 (imprime 4) * 2 (imprime 8) # (termine l’application)

On doit gérer le plus d’erreur d’utilisateur possible, comme :

- Utilisation d’un opérateur inconnu (Indiquer l’erreur et revenir au point 2)
- Division par 0 (Indiquer l’erreur et revenir au point 2)
- Etc.

## Exigences

Chaque exigence pas respectée enlève 0.1 de la note du TP.

- [x] Le code contient au minimum un fichier header et deux fichiers source
- [x] Le code doit contenir au moins une classe
- [x] Il faut utiliser au moins un enum, un pointeur et une référence 
  - Même si cela n’est peut-être pas nécessaire, c’est un exercice après tout.
- [x] Le code compile avec le flag -Werror compilateur et l’option –Wall en utilisant GCC
- [x] Le projet doit contenir un .gitlab-ci.yml qui compile l’outil après chaque commit pour vérifier qu’il compile
  correctement et qui exécute un programme de test
- [x] Votre repository ne doit pas contenir des fichiers binaires et temporaires (comme le répertoire cmake-build-debug, .idea etc.)
- [x] Vous travaillez sur un fork du repo original (lien sur cyberlearn) et devez créer un merge request avant la
  deadline.

## Optionnel

Les buts suivants sont optionnels

- [x] Ajout opérateurs supplémentaires
- [x] Permettre l’utilisation de constantes comme e ou π
- [x] Permettre l’utilisation de parenthèses
- [x] Compiler le programme dans le CI non seulement avec GCC, mais aussi Clang (ou d’autres compilateurs)
