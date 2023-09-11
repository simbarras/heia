% =========================================================================
% FILE     : neg.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Techniques Avancées de Programmation 3 : Programmation Logique
% =========================================================================
% OVERVIEW : Negation in Prolog
% =========================================================================

not(P) :- P, !, fail.
not(_).
% ------------------------------------------------------------
prime(2).
prime(3).
prime(5).

even(4).
even(2).

a(X)   :- prime(X).
a(8).
b(X)   :- prime(X), !.
b(8).
c(X)   :- !, prime(X).
c(8).
d(X)   :- prime(X), even(X), !.
d(8).
e(X)   :- even(X), !, prime(X).
e(8).
f(X)   :- even(7), !, X=7.
f(8).
g(X,Y) :- prime(X), even(Y), !.
g(8,8).
h(X,Y) :- prime(X), !, even(Y).
h(8,8).
i(X,Y) :- even(Y), !, prime(X).
i(8,8).

% ------------------------------------------------------------
% ---    owns(+L, ?E) : E est un élément de la liste L

owns([X|_Xs], X).
owns([_Y|Ys], X) :-  owns(Ys, X) .

% ----------------------
owns1(Ys, X)     :- egal(Ys, [_|Ys1]), owns1(Ys1, X).
owns1([X|_], Y)  :- egal(Y,X).
owns2([X|_], Y)  :- egal(X,Y).
owns2(Ys, X)     :- owns2(Ys1, X),  egal(Ys,[_|Ys1]).	
owns3([X|_], X).
owns3([_|Ys], X) :- owns3(Ys, Z),  egal(X,Z).
owns4([X], X).
owns4([X,_], X).
owns4([_|Ys], X) :- owns4(Ys, X).
egal(A,A).	



% ------------------------------------------------------------
% --- myTrue() : prédicat true\0 sans implémenter les prédicats prédéfinis
myTrue.


myFalse:-
    myEgal(0, 1).

myEgal(X, X).




t :- var(E),
    not(p(E)),
    nonvar(E).

p(X) :-
     X = 1,
     !,
    fail.