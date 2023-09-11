% =========================================================================
% FILE     : family.pl
% SUPPORT  : Bapst Frederic, EIA-FR.
% CONTEXT  : Techniques Avancées de Programmation 3 : Programmation Logique
% =========================================================================
% OVERVIEW : First steps in Prolog
% =========================================================================

% ------------------------------------------------------------
%--- parent(X,Y) : Y has parent X (ie X is a parent of Y)
parent(sam, jack).         parent(jack, jim).
parent(jack, jill).        parent(jim, ben).
parent(sandy, jack).

man(sam). 
man(jack). 
man(jim). 
man(ben).

woman(sandy). 
woman(jill).

%--- son(X, Y) : X is a son of Y
son(X,Y) :- parent(Y,X), man(X).

%--- ancestor(X, Y) : X is an ancestor of Y
ancestor(Ancestor, Descendant) :-
     parent(Ancestor, Descendant).
ancestor(Ancestor, Descendant) :-
     parent(Ancestor, X),
     ancestor(X, Descendant).

% ------------------------------------------------------------
% ------------------------------------------------------------
% Tourne le moulin, fait voler la farine,
% Pendant ce temps, meunier content,
% Chante en travaillant.

works(etudiant).
works(prof).
works(meunier).

happy(etudiant).
happy(boucher).
happy(meunier).
happy(clown).
happy(banquier).

turns(moulin).

flies(farine).
flies(oiseau).

sings(oiseau).
sings(meunier).

