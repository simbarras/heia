% =========================================================================
% FILE     : quicksort.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Techniques Avancées de Programmation 3 : Programmation Logique
% =========================================================================
% OVERVIEW : Quicksort with lists in Prolog
% =========================================================================


% ------------------------------------------------------------
%--- partition(+P, +Ls, ?Ss, ?Gs) : Ss and Gs gives a correct partition
%                                   of Ls with Ss <= P, Gs > P

% Pseudo-code : mettre le 1er élt de Ls dans Ss ou Gs, et relancer

partition(P, SLs, [SLs1|Ss], Gs) :-
    SLs = [SLs1|SSLs],
    P >= SLs1,
    partition(P, SSLs, Ss, Gs).
partition(P, SLs, Ss, [SLs1|Gs]) :-
    SLs = [SLs1|SSLs],
    P < SLs1,
    partition(P, SSLs, Ss, Gs).
partition(_, [], [], []).

% A COMPLETER

% ------------------------------------------------------------
%--- quickSort(+Ls, ?Ss) : Ss is a sorted version of Ls
% Pseudo-code : prendre comme pivot P le 1er élt de Ls,
%               partitionner le reste en As, Bs
%               trier les 2 parties AsSorted, BsSorted
%               concaténer AsSorted, P, BsSorted
%quickSort([Ls1|[]], Ls1).

quickSort([],[]).
quickSort([P|Ls],Ss) :-
    partition(P,Ls,As,Bs),
    quickSort(As,AsSorted),
    quickSort(Bs,BsSorted),
    append(AsSorted,[P|BsSorted],Ss).



qsorttest :- 
	Ls=[4,5,3,6,8,2], 
	write('   Input list: '), write(Ls), nl,
	quickSort(Ls, Res), 
	write('Sorted result: '), write(Res).
