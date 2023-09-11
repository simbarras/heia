% =========================================================================
% FILE     : lists.pl
% SUPPORT  : Bapst Frederic, EIA-FR.
% CONTEXT  : Cours Programmation Logique
% =========================================================================
% OVERVIEW : Exercising lists in Prolog
% =========================================================================

%----------- S02 - Ex 1) ---------------------

%--- Vérification grâce au prédicat suivant
ex1 :-
	write('                                [a,b,c,d] : '), write([a,b,c,d]), nl,
	write('                              [a,b|[c,d]] : '), write([a,b|[c,d]]), nl,
	write('                             [a,b,c,d|[]] : '), write([a,b,c,d|[]]), nl,
	write('                            [[[a,b,c,d]]] : '), write([[[a,b,c,d]]]), nl,
	write('                            [[a,b],[c,d]] : '), write([[a,b],[c,d]]), nl,
	write('                            [[a,b]|[c,d]] : '), write([[a,b]|[c,d]]), nl,
	write('                        [[a],[b],[c],[d]] : '), write([[a],[b],[c],[d]]), nl,
	write('                          [[a,b]|[[c,d]]] : '), write([[a,b]|[[c,d]]]), nl,
	write('                              [[a,b],c,d] : '), write([[a,b],c,d]), nl,
	write('\'.\'( \'.\'(a, \'.\'(b,[])), \'.\'(c,\'.\'(d,[]))) : '), write('.'( '.'(a, '.'(b,[])), '.'(c,'.'(d,[])))), nl,
	nl.

%----------- S02 - Ex 2) ---------------------

f(As,     As,    [a,b]  ) :-
	g(As, _).
f([A|As], [b,A], As     ) :- 
	g(A, As).
f([],     Bs,    [Bs|Bs]).
g(_, []).

%----------- S02 - Ex 3) ---------------------

% --- listConcat(?Xs, ?Ys, ?XsYs) : XsYs is the concatenation of Xs and Ys
listConcat([], Ys, Ys).
listConcat(Xs, [], Xs).
listConcat([X|Xs], Ys, [X|R]) :-
    listConcat(Xs, Ys, R).


% --- listSuffix(+L, ?S) : S is a suffix of L
listSuffix(Xs, []).
listSuffix(Xs, Xs).
listSuffix([X|Xs], S) :-
    listSuffix(Xs, S).


% TODO -  A COMPLETER

% --- listRightTrim(+L, ?T) : T is L whithout its last elt
listRightTrim([_], []).
listRightTrim([X|Xs],[X|Ys]) :- listRightTrim(Xs,Ys).

% --- listPrefix(+L, ?P) : P is a prefix of L
listPrefix([X|Xs],[X|Ys]) :- listPrefix(Xs,Ys).
listPrefix(_, []).

%----------- S02 - Ex 4) ---------------------

%---    owns(+L, ?E) : E est un élément de la liste L
owns([X|_Xs], X).
owns([_Y|Ys], X) :- owns(Ys, X).

%----------- S02 - Ex 5) ---------------------

% --- listSublist(+L, ?S) : S is a sublist of L

% ----------------------
listSublist1(Ls, Ss) :-
    listSuffix(Ps, Ss),
    listPrefix(Ls, Ps).

% ----------------------
listSublist2(Ls, Ss) :-
    listPrefix(Ls, Ps),
    listSuffix(Ps, Ss).

% ----------------------
listSublist3(Ls, Ss) :-
    listSuffix(Ls, Sufs),
    listPrefix(Sufs, Ss).

%----------- S02 - Ex 6) ---------------------

% --- unifyMany(?As, ?Bs) : As and Bs are list of terms (of same length),
%                           and each Aj is unified with Bj
unifyMany([], []).
unifyMany([A|As], [B|Bs]) :-
    A = B,
    unifyMany(As, Bs).


