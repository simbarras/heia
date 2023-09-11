% =========================================================================
% FILE     : itrlist.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Techniques Avancées de Programmation 3 : Programmation Logique
% =========================================================================
% OVERVIEW : Abstract Data Types in Prolog - Iterative lists
% =========================================================================

% ------------------------------------------------------------
% --- Test programs
% ------------------------------------------------------------
adt_test_apply(_, Adt, [], Adt).
adt_test_apply(G, Adt, [C|Cmds], AdtEnd) :-
        Goal =.. [G, Adt, C, Adt1],
        write(trying(Goal)), nl,
        Goal,
        write(done(Goal)), nl,
        adt_test_apply(G, Adt1, Cmds, AdtEnd).
% ------------------------------------------------------------
itrList_test :-
        itrList_new(ItrList),
        Cmds = [insertAfter(a),
                insertAfter(b),
                goToNext,
                consultAfter(a),
                removeAfter(a),
                isLast,
                insertAfter(d),
                goToPrev,
                consultAfter(B)
               ],
        adt_test_apply(itrList_apply, ItrList, Cmds, ItrList1),
     \+ itrList_apply(ItrList1, isLast,_),
        assertEquals(b, B),
        write('Test passed successfully.').

assertEquals(Expected, Effective) :-
	Expected==Effective.
assertEquals(Expected, Effective) :-
	Expected\==Effective,
	write('bad news... Expected: '), write(Expected),
	write(' Effective: '), write(Effective), nl, 
	fail.
% ------------------------------------------------------------
% itrList : Iterative List ADT, with current arc
%    Operations :
%      new(-X),  goToNext,  insertAfter(+X),
%      isFirst,  goToPrev, consultAfter(?X), 
%      isLast,              removeAfter(?Removed)
%    Representation : 
%      e(ListTowardsFirst, ListTowardsEnd)

% itrList_new(...).
itrList_new(e([],[])).

% itrList_apply(..., goToNext, ...).
itrList_apply(e(Xs,[Y|Ys]), goToNext, e([Y|Xs],Ys)).

% itrList_apply(..., goToPrev, ...).
itrList_apply(e([X|Xs],Ys), goToPrev, e(Xs,[X|Ys])).

% itrList_apply(..., insertAfter(+X), ...).
itrList_apply(e(Xs,Ys), insertAfter(Y), e(Xs,[Y|Ys])).

% itrList_apply(..., consultAfter(?X), ...).
itrList_apply(e(Xs,[Y|Ys]), consultAfter(Y), e(Xs,[Y|Ys])).

% itrList_apply(..., removeAfter(?X), ...).
itrList_apply(e(Xs,[Y|Ys]), removeAfter(Y), e(Xs,Ys)).

% itrList_apply(..., isFirst, ...).
itrList_apply(e([],Ys), isFirst, e([],Ys)).

% itrList_apply(..., isLast, ...).
itrList_apply(e(Xs,[]), isLast, e(Xs,[])).