# ITIV groupe 7

Le groupe 7 est composé de : Barras Simon, Rojas David et Steiger Damien et le sujet choisi est le mosaicing-plan (5).

## Problématique

Nous avons choisi le thème numéro 5 qui consiste à reconstruire une image d'un plan à l'aide de plusieurs images.

Les objectifs de ce mini projet sont les suivants :
 - Lire n images d'un plan (au minimum 3)
 - Créer des pairs de points pour associer les 2 images
 - Calculer la matrice de l'homographie avec OpenCV et à la main
 - Construire l'image mosaïque

## Utilisation

Notre projet se lance à l'aide d'arguments. Voici la commande pour avoir la liste: `python main.py -h`

```bash
usage: main.py [-h] [-a] [-c] [-d DIRECTORY] [-o OUTPUT] [images ...]

Tool to merge images together

positional arguments:
  images                Images to merge

options:
  -h, --help            show this help message and exit
  -a, --automatic       Demonstrate the Mosaicing-plan with pre-selected
                        points
  -c, --custom          Use our homography computation instead of opencv
  -d DIRECTORY, --directory DIRECTORY
                        Directory containing the images to merge
  -o OUTPUT, --output OUTPUT
                        Output file name
```