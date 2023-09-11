% =========================================================================
% OVERVIEW : State graphs. Forward chaining. Missionaries & cannibals problem
% =========================================================================

:- include('stateGraph.pl').  

%=======================================================================
% --- problem name: cannibals(N, BoatCapacity)
%                   for N missionaries + N cannibals, 
%                   and a boat for [1..BoatCapacity] persons
% --- state: cannib(BoatPosition, left(M,C),  right(M,C), capacity(Cap) )
%                   BoatPosition: left/right
%
% --- transitions: move(nbOfMission, nbOfCannib)

initialState(cannibals(N, Cap), cannib(left, left(N,N), right(0,0), capacity(Cap))).

finalState(cannib(right, left(0, 0), _, _)).

legalStateSide(0, _).
legalStateSide(M, C):-
    M >= C.

legalState(cannib(_, left(Ml,Cl), right(Mr,Cr), _)) :-
    legalStateSide(Ml, Cl),
    legalStateSide(Mr, Cr).

%  from left to right
transition(cannib(left, left(Ml, Cl), right(Mr, Cr), capacity(C)), moveRight(Nm, Nc), cannib(right, left(Nml, Ncl), right(Nmr, Ncr), capacity(C))):-
    nbBetween(0, Ml, Nm),
    Nm =< C,
    nbBetween(0, Cl, Nc),
    Nc =< C - Nm,
    (Nm + Nc) > 0,
    Nml is Ml - Nm,
    Ncl is Cl - Nc,
    Nmr is Mr + Nm,
    Ncr is Cr + Nc.

%  from right to left
transition(cannib(right, left(Ml, Cl), right(Mr, Cr), capacity(C)), moveLeft(Nm, Nc), cannib(left, left(Nml, Ncl), right(Nmr, Ncr), capacity(C))):-
    nbBetween(0, Mr, Nm),
    Nm =< C,
    nbBetween(0, Cr, Nc),
    Nc =< C - Nm,
    (Nm + Nc) > 0,
    Nml is Ml + Nm,
    Ncl is Cl + Nc,
    Nmr is Mr - Nm,
    Ncr is Cr - Nc.


%=======================================================================
%--- nbBetween(+From, +To, ?N) :- N is an int between From and To
nbBetween(From, _To, From).
nbBetween(From, To, N) :- 
    From < To,
    From1 is From + 1,
    nbBetween(From1, To, N).


go(M) :-
    Goal=solveStateProblem(cannibals(3, 2), M),
    write(Goal), nl,
    Goal.


goBfs(M) :-
    Goal=solveBfs(cannibals(3, 2), M),
    write(Goal), nl,
    Goal.