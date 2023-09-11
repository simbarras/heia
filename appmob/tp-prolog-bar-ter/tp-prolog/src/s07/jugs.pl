% =========================================================================
% OVERVIEW : State graphs. Forward chaining. Water jugs problem
% =========================================================================

:- include('stateGraph.pl'). 

%=======================================================================
% --- problem name: jugs(CapacityA, CapacityB, WantedLiters)
%
% --- state: jugs(contents(A,B), capacity(CapA,CapB), target(Wanted))
%
% --- transitions: empty(a), empty(b), fill(a), fill(b), 
%                  transfer(a,b), transfer(b,a)

initialState(jugs(A,B,T), S) :-
    S = jugs(contents(0,0), capacity(A,B), target(T)).
    
finalState(jugs(contents(T,_), _, target(T))).
finalState(jugs(contents(_,T), _, target(T))).

% it will be checked in the transitions...
legalState(jugs(_,_,_)).  % Legal state is not defined beacause transfert is capacity safe

% Empty A
transition(jugs(contents(_,Vb), C, T), empty(a), jugs(contents(0, Vb), C, T)).

% Empty B
transition(jugs(contents(Va, _), C, T ), empty(b), jugs(contents(Va, 0), C, T)).

% Fill A
transition(jugs(contents(_, Vb), capacity(Ca, Cb), T), fill(a), jugs(contents(Ca, Vb), capacity(Ca, Cb), T)).

% Fill B
transition(jugs(contents(Va, _), capacity(Ca, Cb), T), fill(b), jugs(contents(Va, Cb), capacity(Ca, Cb), T)).

% Transfer A to B
transition(jugs(contents(Va, Vb), capacity(Ca, Cb), T), transfer(a, b), Nj):-
    Va + Vb > Cb,
    Nb = Cb,
    Na is Va + Vb - Cb,
    Nj = jugs(contents(Na, Nb), capacity(Ca, Cb), T).

transition(jugs(contents(Va, Vb), capacity(Ca, Cb), T), transfer(a, b), Nj):-
    Cb >= Va + Vb,
    Nb is Va + Vb,
    Na = 0,
    Nj = jugs(contents(Na, Nb), capacity(Ca, Cb), T).

% Transfert B to A
transition(jugs(contents(Va, Vb), capacity(Ca, Cb), T), transfer(b, a), Nj):-
    transition(jugs(contents(Vb, Va), capacity(_, Ca), _), transfer(a, b), jugs(contents(Nb, Na), _, _)),
    Nj = jugs(contents(Na, Nb), capacity(Ca, Cb), T).

%----------------------------
go(M) :-
    Goal=solveStateProblem(jugs(8,5,7), M),
    write(Goal), nl,
    Goal.
