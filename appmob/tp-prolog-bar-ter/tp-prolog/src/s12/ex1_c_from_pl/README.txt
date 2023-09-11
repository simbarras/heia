#--- BUILD & RUN

En supposant que les dossiers gcc+gprolog figurent dans le PATH, les
commandes suivantes créent un interprète "enrichi", puis le démarre : 

  $ gplc augm.pl examp_c.c
  $ ./augm.exe

#--- INSTALLATION

Pour ce tout dernier TP Prolog, il faut un compilateur C adapté, en 
veillant à l'adéquation entre gprolog et le compilateur C sur deux 
points : <MSVC++ vs GCC>, et <32bit vs 64bit>. 

Sur Linux/Mac j'imagine que gcc est prêt; sur Windows, une option possible 
est MinGW (maybe déjà installé pour le cours "Advanced Java" ?) 
Voici un toolchain que j'avais testé sous Windows :
 
- gprolog: setup-gprolog-1.4.5-mingw-x64.exe

- MinGW-w64: download 
https://github.com/niXman/mingw-builds-binaries/releases/download/12.2.0-rt_v10-rev1/x86_64-12.2.0-release-posix-seh-rt_v10-rev1.7z

- ajouter dans le PATH les 3 dossiers suivants :
    …\mingw64\bin
    …\mingw64\x86_64-w64-mingw32\bin
    …\GNU-Prolog\bin

Il y a bien sûr d'autres manières de s'organiser : 
- utiliser une machine virtuelle Linux (p. ex. via VirtualBox)
- s'appuyer sur WSL (Windows Subsystem for Linux)
- installation complète de MSYS2/MinGW — parfois j'avais dû 
  cloner   …\msys2_64\mingw64\bin\as.exe
       en  …\msys2_64\mingw64\bin\x86_64-w64-mingw32-as.exe 

Rappel: les noms de dossiers contenant des espaces causent parfois 
des ennuis — mais qui donc irait nommer un dossier "Program Files" ?

