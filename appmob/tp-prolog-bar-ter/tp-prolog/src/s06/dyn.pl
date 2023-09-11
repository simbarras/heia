% =========================================================================
% FILE     : dyn.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : assert/retract in Prolog, findall/3, and "univ" =..
% =========================================================================


% ------------------------------------------------------------
:- dynamic(hanoiDyn/5).                    % *** Ex. 1 ***

% --- hanoiDyn1(+NbOfDiscs, ?A, ?B, ?C, ?NbOfMoves).
% --- Avec assert/retract, nbre de mouvements

hanoiDyn(1,_A,_B,_,1.0).                      %--- move _A to _B
hanoiDyn(N,A,B,C,Moves) :-
            N>1,
            N1 is N-1,
            hanoiDyn(N1,A,C,B,Moves1),        %--- move N-1 discs
   asserta((hanoiDyn(N1,A,C,B,Moves1) :-! )),
                                              %--- move A to B
            hanoiDyn(N1,C,B,A,Moves2),        %--- move N-1 discs
   retract((hanoiDyn(N1,A,C,B,_     ) :-! )),
            Moves is Moves1 + Moves2 + 1.

% ------------------------------------------------------------
solve1(N, Moves) :- 
	[A, B, C] = [a, b, c],          % L2
	hanoiDyn(N, A, B, C, Moves).    % L1
solve2(N, Moves) :- 
	hanoiDyn(N, A, B, C, Moves),    % L1
	[A, B, C] = [a, b, c].          % L2
	
% ------------------------------------------------------------
% --- organize(...) : ????                 % *** Ex. 2 ***

organize([X|Xs], Ys) :- 
        add(X, []), 
        organize(Xs, Ys).
organize([], Ys) :- 
        wrap(Ys).
% ----------------
wrap([X|Xs]) :- 
        elt(X),  
        convert(X, G),
        retract(G), 
        !,
        wrap(Xs).
wrap([]).
% ----------------
add(X, Gs):- 
        elt(Y), 
        Y<X, 
        convert(Y, G),
        retract(G), 
        !,
        add(X, [G|Gs]).
add(X, Gs):- 
        convert(X, G), 
        enrich([G|Gs]).

enrich([]).
enrich([G|Gs]) :- 
        asserta(G), 
        enrich(Gs).
% ----------------
:- dynamic(elt/1).

convert(X, elt(X)).

% ------------------------------------------------------------
% --- fol(...) : ???                       % *** Ex. 3 ***
fol([], _, A, Res) :- Res is A.
fol([E|Ls], Op, Crt, Res) :-
    Crt1 =.. [Op, Crt, E],
    fol(Ls, Op, Crt1, Res).

% ------------------------------------------------------------
%                                          % *** Ex. 4 ***
% Use predefined predicates:  
%   nth/3, length/2, asserta/1, nonvar/1

:- dynamic(edge/4).       %--- edge(GraphName, From, To, Weight)
:- dynamic(graphOrder/2). %--- graphOrder(GraphName, NbOfVertices)
%--- fromAdjacencyMatrix(+Gr). Gr = graph(GraphName, AdjMatrix)
fromAdjacencyMatrix(Gr) :-
    Gr=graph(Gn, Gm),
    length(Gm, N),
    asserta(graphOrder(Gn, N)),
    findall(_, anEdge(Gr), _).

%--- anEdge(Gr): an edge is asserted as a fact.
anEdge(Gr) :-
    Gr=graph(Gn, Gm),
    nth(I, Gm, L),
    nth(J, L, W),
    nonvar(W),
    asserta(edge(Gn, I, J, W)).

    
fromAdjMatrixDemo :- 
    M = [[_,_,3,2],   % edges from vertex no 1
         [_,_,5,_],   % edges from vertex no 2
         [1,_,_,8],   % edges from vertex no 3
         [_,7,_,_]],  % edges from vertex no 4
    fromAdjacencyMatrix(graph(g1, M)).


/*--------------- Expected behaviour: ------------
| ?- fromAdjMatrixDemo.
yes
| ?- listing.
[...]
graphOrder(g1, 4).
edge(g1, 4, 2, 7).
edge(g1, 3, 4, 8).
edge(g1, 3, 1, 1).
edge(g1, 2, 3, 5).
edge(g1, 1, 4, 2).
edge(g1, 1, 3, 3).
yes
  ------------------------------------------------*/
