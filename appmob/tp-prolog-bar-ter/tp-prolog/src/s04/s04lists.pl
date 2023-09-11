% =========================================================================
% FILE     : s04lists.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : Lists in Prolog, accumulators
% =========================================================================

% ------------------------------------------------------------
%--- factorial(+N, ?F) : F is the factorial of N

factorial(0, 1).
factorial(N, F) :- 	
        N>0, 
        N1 is N - 1,
        factorial(N1, F1),
        F is F1 * N.

% factorial with accumulator : F is the factorial of N
factorial_acc(N, F) :-
    factorial_acc(N , 1, F).

factorial_acc(0, F, F).
factorial_acc(N, Acc, F) :-
    N > 0,
    N1 is N - 1,
    A1 is Acc * N,
    factorial_acc(N1, A1, F).


% ------------------------------------------------------------
%--- listSum_acc(+Ls, ?S) : Ls is a list of ints, S is the sum
%--- With accumulator
listSum_acc(Ls, S):-
    listSum_acc(Ls, 0, S).

listSum_acc([], S, S).
listSum_acc([L|Ls], Acc, S):-
    A1 is Acc + L,
    listSum_acc(Ls, A1, S).

% ------------------------------------------------------------
%--- listSum(+Ls, ?S) : Ls is a list of ints, S is the sum
listSum([], S):-
    S is 0.
listSum([L|Ls], S):-
    listSum(Ls, S1),
    S is S1 + L.
% ------------------------------------------------------------


% ------------------------------------------------------------
% --- concat(?As, ?Bs, ?Cs, ?AsBsCs) : 
%        AsBsCs is the concatenation of As, Bs and Cs
% ---
concat1(As, Bs, Cs, Ls) :-
    append(As, Bs, Xs),
    append(Xs, Cs, Ls).
% ---
concat2(As, Bs, Cs, Ls) :-
    append(Xs, Cs, Ls),
    append(As, Bs, Xs).
% ---
concat3(As,Bs,Cs,Ls) :- var(Ls),
    concat1(As,Bs,Cs,Ls).
concat3(As,Bs,Cs,Ls) :- nonvar(Ls),
    concat2(As,Bs,Cs,Ls).

% ------------------------------------------------------------
% --- Ex. 4
% |?- countAB('t.txt').
%   le fichier t.txt contient 3 a et 25 b
% |?- countAB('noFile.zzz').
%   Oups, erreur I/O
% 
% Please verify the behavior of your code (there should be only _one_ solution!)
countAB(F) :-
    catch(open(F, read, S), E, write('Oups, erreur I/O')),
    catch(get_char(S, C), E, write('Oups, erreur I/O')),
    catch(countAB(S, C, 0, 0, Ar, Br, a, b), E, write('unknown error')),
    close(S),
    write('le fichier '), write(F), write(' contient '),
    write(Ar), write(' a et '), write(Br), write(' b'), nl.

countAB(S, C, An, Bn, Ar, Br, A, B) :-
    C = end_of_file,
    Ar is An + 0,
    Br is Bn + 0.
countAB(S, C, An, Bn, Ar, Br, A, B) :-
    C \= end_of_file,
    increment(C, An, A, An1),
    increment(C, Bn, B, Bn1),
    get_char(S, C1),
    countAB(S, C1, An1, Bn1, Ar, Br, A, B).


increment(C, Xn, X, Xn1):-
    C = X,
    Xn1 is Xn + 1.
increment(C, Xn, X, Xn1):-
    C \= X,
    Xn1 is Xn.



    
% ------------------------------------------------------------
% --- p(+Ls, ?Rs): ...
% --- p(?Ls, +Rs).

p(Ls, Rs) :-
    var(Ls),
    pAlg(Rs, Ls).
p(Ls, Rs) :-
    nonvar(Ls),
    pAlg(Ls, Rs).
pAlg(Ls, [E|Rs]) :-
    append(As, [E|Bs], Ls),
    append(As, Bs, Xs),
    pAlg(Xs, Rs).
pAlg([], []).

p2(Ls, Rs) :-
    var(Ls),
    pAlg2(Rs, Ls).
p2(Ls, Rs) :-
    nonvar(Ls),
    pAlg2(Ls, Rs).
pAlg2(Ls, [E|Rs]) :-
    select(E, Ls, Xs),
    pAlg2(Xs, Rs).
pAlg2([], []).
