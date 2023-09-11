Il y a quelques outils à installer pour le  cours "Programmation Logique". Je
vous invite à effectuer les tâches suivantes :

- S'inscrire au cours "Prolog" sur Moodle :
     https://cyberlearn.hes-so.ch/course/view.php?id=145
     [au besoin, clé d'inscription = 1970 ]
     
- Installer GnuProlog depuis http://www.gprolog.org/
  Pour Windows, typiquement : http://www.gprolog.org/setup-gprolog-1.5.0-mingw-x64.exe
  Pour Linux ou Mac(*), il ne devrait pas y avoir de problème, au besoin
  entraidez-vous.

- Vous souhaiterez sûrement mettre en place l'accès Git (voir aussi plus bas) :
  https://gitlab.forge.hefr.ch/frederic.bapst/tp-prolog.git

- IDE : En fait, pour qui est habitué-e à la ligne de commande, pas
  besoin d'environnement sophistiqué : un bon éditeur de texte
  suffit tout à fait... Sinon, deux solutions à choix sont proposées :

  * IntelliJ-IDEA : Un plugin (assez récent projet de semestre) est disponible :
    - Télécharger depuis : http://frederic.bapst.home.hefr.ch/intelliprolog
    - IDEA -> Settings -> Plugins -> Install from disk -> IntelliProlog*.zip
    - Créer un SDK pour Prolog :
        File -> Project Structure -> SDKs -> +(Add new SDK) -> GNU Prolog
             -> localiser l'exécutable ...\Gnu-Prolog\bin\gprolog.exe
    - Importer depuis Git le projet tp-prolog:
        File -> New -> Project from version control... -> git
             -> https://gitlab.forge.hefr.ch/frederic.bapst/tp-prolog.git
      Puis associer le SDK Prolog à ce projet :
        File -> Project Structure -> Project SDK

    Merci de me transmettre vos éventuelles remarques/suggestions/bugReports.
    [Known issue: la commande "Load in gProlog within IDE console" est
     plutôt à éviter, surtout sous Windows. ]

  * Eclipse : Un plugin (très vieux projet de semestre) est disponible :
    - Ouvrir Eclipse -> Help -> Install new software -> update site :
         http://frederic.bapst.home.hefr.ch/tap/eclipse/update
    - Windows -> Preferences -> Prolog, adapter si nécessaire le chemin vers
         ...\Gnu-Prolog\bin\gprolog.exe
    - Importer depuis Git le projet tp-prolog:
      File -> Import -> Git -> Projects from git -> Clone URI
           -> https://gitlab.forge.hefr.ch/frederic.bapst/tp-prolog.git

Good luck!


(*) A tout hasard : pour les utilisateurs Mac, un étudiant avait un jour
    rapporté la recommandation suivante (si c'est utile) :
>    Ajouter à la variable d'environnement $PATH le chemin vers le dossier
>    /opt/local/lib/gprolog-1.5.0/bin
>
>    Pour que la modification de la variable persiste, il faut l'exporter
>    en ajoutant la ligne suivante dans le fichier «~/.bash_profile»
>
>        export PATH="$PATH:/opt/local/lib/gprolog-1.5.0/bin"
>
>   Il faut «exécuter» (source) le fichier une première fois à l'aide de
>   la commande (car il n'est exécuté qu'au démarrage du système) :
>
>       ~/.bash_profile






