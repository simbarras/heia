% =========================================================================
% FILE     : bb_sorting.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : Blackboard program for sorting
% =========================================================================

:- include('bb.pl').

%---------------------------------------------------------
%--- INPUT : set of numbers, and the term n(0)
%--- OUTPUT: set of terms e(Index,Value) giving the ascending order
%---------------------------------------------------------

[X, n(N)] / [number(X)] ~~> [N1 is N+1] / [e(N1,X), n(N1)].
[e(Ix, Ex), e(Iy, Ey)] / [Ex < Ey, Ix > Iy] ~~> [] / [e(Iy, Ex), e(Ix, Ey)].

%[X, n(N)] / [number(X)] ~~> [N1 is N+1] / [e(1,X), n(N1)].
%[e(I, Ex), e(I, Ey)] / [Ex < Ey] ~~> [I1 is I + 1] / [e(I, Ex), e(I1, Ey)].



test_bb_sorting :- BB=[ 8,2,5,9, n(0) ], 
                   bb_run(BB,BB1),nl,write(BB1). 

% should be : [e(0,2),e(3,9),n(4),e(1,5),e(2,8)]
