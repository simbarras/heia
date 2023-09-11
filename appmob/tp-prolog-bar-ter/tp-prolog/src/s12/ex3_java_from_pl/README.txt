Quelques rappels sur Java 
-------------------------

- Commande de compilation :  
    $ javac MyPrg.java
  on donne le nom du fichier source
  ceci produit un fichier MyPrg.class

- Lancement de la JVM :    
    $ java MyPrg
  on donne le nom d'une classe (contenant main())
  Si vous avez mis votre classe dans un package s12, 
  alors ça devient : 
    $ java s12.MyPrg
  Attention, le fichier MyPrg.class doit alors être placé
  dans un dossier s12, lequel doit être accessible depuis le
  classpath (qui, par défaut, inclut probablement le répertoire 
  courant "."). 

- Lancement de la démo en Prolog : 
  Il faut être attentif au répertoire courant (probablement celui 
  qui contient votre fichier *.pl), charger (consult(…)) votre
  programme Prolog, et poser la question : 
    | ?- go.
  On peut vérifier le répertoire courant avec : 
    | ?- working_directory(A).
  
    