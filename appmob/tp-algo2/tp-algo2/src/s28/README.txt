S28 - Bytecode
==============

- Cet exercice est un prolongement de la série s15 (Optimisation de code,  
  Traveling Salesman Problem).

- La librairie asm-[version].jar est à ajouter comme dépendance du projet.
  [ Une façon sur IntelliJ-IDEA: right-click sur *.jar -> Add as library -> … 
    Vérifier ou mettre à jour : Project Structure -> Project Settings ->
                                Module -> Dependencies
  ]
  
- Pour produire TSP200Dump.java, j'avais écrit une classe TSP200.java (non
  fournie, mais montrée sur l'énoncé), qui a ensuite été normalement compilée, 
  et le bytecode obtenu a été repris par l'utilitaire `asmify`.
  
- Vous devez optimiser le bytecode (et non pas le code Java d'origine !), en 
  repérant où le compilateur a produit des "motifs de bytecode" qui peuvent
  être améliorés. Faites une seule modification à la fois, en sauvant vos 
  versions successives - penser à renommer en TSP20[1,2,…] aussi bien le *Dump
  que le nom interne de la classe produite (constante DUMPED_CLASSNAME).
  
- J'ai inclus un PDF en guise de "bytecode quick reference". [Je ne vois aucun
  intérêt à apprendre par coeur une telle liste. A travers cette série, 
  l'objectif est de comprendre les concepts généraux, et d'être capable 
  de trouver la signification d'un extrait de bytecode similaire.]
  
- Exécuter TSP200Dump.java a pour effet de créer (dans le répertoire courant)
  un fichier TSP200.class. Bien sûr, ce n'est pas à cet endroit qu'un tel
  fichier doit être placé si on veut l'utiliser comme les autres (TSP11 etc.) 
  quand on lance le programme TSPTest. C'est l'occasion de bien comprendre
  ce que fait en coulisses votre IDE, et de revoir la notion de CLASSPATH
  (avec aussi la hiérarchie de dossiers qui correspondent aux packages).
  Suivant la configuration, les fichiers compilés sont prévus dans un dossier
  tel que out/production/tp-algo2/packageName/...
  
- En l'état, la classe produite par TSP200Dump est tsp.TSP200 (donc dans
  le package tsp, celui qu'on a déjà utilisé lors de la série s15); une fois
  le fichier TSP200.class déplacé au bon endroit, on peut donc inclure son 
  numéro dans le programme tsp.TSPTest, pour mesurer les performances.
  
- Naturellement, en composant soi-même la suite d'instructions de bytecode, on 
  risque de produire du code corrompu qui sera sèchement refusé par la JVM 
  durant l'exécution. ASM fournit une méthode `verify()` qui peut parfois
  donner un message renseignant mieux sur l'erreur, mais pour l'utiliser  
  il faut ajouter encore les autres asm-*-*.jar fournis. N'hésitez pas à me
  faire signe en cas de problème (l'une des maladresses possibles est assez 
  fréquente et facile à repérer...)
  
- Assurez-vous de désactiver le JIT (comprenez-vous pourquoi les observations
  peuvent être sensiblement différentes si on laisse le JIT activé ?)
  
Good luck and have fun!
