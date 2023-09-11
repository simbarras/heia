% =========================================================================
% FILE     : bb_pgdc.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : Blackboard program computing the greatest common divisor
% =========================================================================

:- include('bb.pl').

%---------------------------------------------------------
%--- INPUT : set of natural numbers v(X), and a term m(Size)
%            giving the number of element
%    OUTPUT: a term pgdc(R) 
%---------------------------------------------------------
     [v(X),v(Y)]/[X>Y] ~~>  [Z is X-Y]/[v(Y),v(Z)].
[v(X),v(X),m(N)]/[]    ~~> [N1 is N-1]/[v(X),m(N1)].
     [v(X),m(1)]/[]    ~~>          []/[pgdc(X)].


test_bb_pgdc:- 
    BB=[ v(18),v(42),v(72),v(30),m(4) ],
    bb_run(BB,BB1), nl, 
    write('Expected: '), write([pgdc(6)]), 
    write(BB1).
