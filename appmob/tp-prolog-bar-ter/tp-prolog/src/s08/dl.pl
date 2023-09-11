% =========================================================================
% FILE     : dl.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Techniques Avancées de Programmation 3 : Programmation Logique
% =========================================================================
% OVERVIEW : D-Lists in Prolog
% =========================================================================

% ------------------------------------------------------------
% --- concatDL(+Xs, +Ys, ?Zs) : Zs is the concatenation of
%                               Xs and Ys (repr. as D-lists)
% --- concatDL(Xs-DXs, Ys-DYs, Zs-DZs) :- DXs=Ys, Zs=Xs, DZs=DYs.
concatDL(Xs-Ys, Ys-DYs, Xs-DYs).

% ------------------------------------------------------------
% myList: a list where you can remove from one extremity, and add at both
%    OPERATIONS: new(-X), consultFirst(?Elt), removeFirst(?Elt) 
%                         addFirst(+Elt), addLast(+Elt)
%              maybe: consultLast(?Elt)
%    REPRESENTATION: based on a D-List
%    COMPLEXITY:     CPU O(1), RAM O(n) 

% --- TODO A COMPLETER

myList_new(Xs-Xs).

myList_apply(Xs-[A|XDs], addLast(A),Xs-XDs).

myList_apply(Xs-XDs, addFirst(A), [A|Xs]-XDs).

myList_apply(Xs-XDs,  removeFirst(A), Ys-XDs) :-
    nonvar(Xs),
    Xs = [A|Ys].

myList_apply(Xs-XDs, consultFirst(A), Xs-XDs):-
    nonvar(Xs),
    Xs=[A|_].


% --------------------------
adt_test_apply(_, Adt, [], Adt).
adt_test_apply(G, Adt, [C|Cmds], AdtEnd) :-
        Goal =.. [G, Adt, C, Adt1],
        write(trying(Goal)), nl,
        Goal,
        write(done(Goal)), nl,
        adt_test_apply(G, Adt1, Cmds, AdtEnd).   
% --------------------------
myList_test :-
        myList_new(Fifo),
        Cmds = [addLast(a), 
                addLast(b), 
                addLast(c),
                consultFirst(X1), 
                addFirst(d),
                consultFirst(X2),
                removeFirst(X3), 
                consultFirst(X4),
                removeFirst(X5), 
                removeFirst(X6), 
                addLast(e),
                removeFirst(X7), 
                removeFirst(X8)
                ],
        [X1,X2,X3,X4,X5,X6,X7,X8]=[a,d,d,a,a,b,c,e],
        adt_test_apply(myList_apply, Fifo, Cmds, Fifo1),
     \+ myList_apply(Fifo1, consultFirst(_),_),
     \+ myList_apply(Fifo1, removeFirst(_),_).


% ------------------------------------------------------------
% --- inverseDL1(+XDs, ?YDs) : YDs is the D-List inverse of D-List XDs


inverseDL1(Xs-XDs, Ys-YDs):-
    unify_with_occurs_check(Xs, XDs),
    unify_with_occurs_check(Ys, YDs).

inverseDL1([X|Xs]-XDs, Rs-YDs) :-
    [X|Xs] == XDs,
    inverseDL1(Xs-XDs, Rs-DRs),
    DRs= [X|YDs].

inverseDL1([X|Xs]-XDs, Rs-YDs) :-
    inverseDL1(Xs-XDs, Rs-DRs),
    DRs= [X|YDs].


%inverseDL1([], _).
%inverseDL1([X|Xs], Ys) :-
%    inverseDL1(Xs, [X|Rs]).





% ------------------------------------------------------------
f_test(Result) :- 
    X = [ [a,b|R], [c,d,e|S], [f,g|T] ] - [R,S,T], 
    Y = [ [h,i|U], [j,k|V], [l,m,n|W] ] - [U,V,W],
    concatDL(X, Y, Result).

%--- g(+CDL, ?Result)
g(([X|As]-[Y])-(Bs-_), X-Y) :- concatDL(_-As, Bs-_, X-Y). 

g_test(Result) :- 
    La  = [ [a,b|A], [c,d,e|B], [f,g|C] | Lad ] - Lad,
    DLa = [A,B,C | DLad] - DLad,
    CDLa = La - DLa,
    g(CDLa, Result).

