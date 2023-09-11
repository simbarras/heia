% =========================================================================
% FILE     : clp.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : Constraint Logic Programming, Finite-Domain in GnuProlog
% =========================================================================

% ------------------------------------------------------------
% Ex. 0 : Quelques exemples apparaissant dans les slides

%--- DONALD + GERALD = ROBERT
donaldDemo(LD):-
        LD=[D,O,N,A,L,G,E,R,B,T],
        fd_all_different(LD),
        fd_domain(LD,0,9),
        fd_domain([D,G],1,9),  %--- Optional... See why ?
           100000*D+10000*O+1000*N+100*A+10*L+D +
           100000*G+10000*E+1000*R+100*A+10*L+D
        #= 100000*R+10000*O+1000*B+100*E+10*R+T,
        fd_labelingff(LD).

% --- maximize:   (3a + 2b)
%     subject to: a+b<50, 4a-b<88, a,b in [0..1000]
optimizeDemo(A,B) :- 
        fd_domain([A,B],0, 50),
            Z #= 3*A+2*B,
          A+B #< 50,
        4*A-B #< 88,
        fd_maximize(fd_labeling([A,B]), Z).


% ------------------------------------------------------------
ex1a :- fd_domain(X,1,5),
        fd_domain(Y,1,10),
        fd_domain(Z,0,5),
        X+2*Y#=Z,
        write('X: '), write(X), nl,
        write('Y: '), write(Y), nl,
        write('Z: '), write(Z), nl.
        
% ------------------------------------------------------------
% Ex. 2a. Petit programme pour mesurer les performances.
%         Poser des questions comme apartBenchmark(10, my_apart_v1)
%                                   apartBenchmark(10, my_apart_v2)
%                             puis essayer avec 15,50,150...
%         (Gnu Prolog indique le temps de calcul s'il dépasse ~10ms)

%--- apartDemo([A,B,C,D,E],z) adds those constraints:
% z(B,A,A), z(C,B,A), z(D,C,A), z(E,D,A),
% z(C,A,B), z(D,B,B), z(E,C,B),
% z(D,A,C), z(E,B,C),
% z(E,A,D),

apartDemo(Ls, Apart) :- apartDemo([], Ls, Ls, Ls, _, Apart).

apartDemo([],       _   , []    , _ , _, _    ).
apartDemo([],       _   , [C|Cs], Ls, _, Apart) :- 
	apartDemo(Cs, Ls, Cs, Ls, C, Apart).
apartDemo([A|As], [B|Bs], Cs    , Ls, D, Apart) :-
	% swap As/Bs so that apart/3 is tested both ways...
    maybeSwap(A,B,A1,B1),
	ApartGoal=..[Apart,A1,B1,D], 
	ApartGoal,  % write(ApartGoal),
	apartDemo(As, Bs, Cs, Ls, D, Apart).

maybeSwap(A,B,B,A) :-
    random(0,2,X), X==0, !.
maybeSwap(A,B,A,B).

apartDemoMain(N, Ls, Apart) :-
	length(Ls, N),
	fd_domain(Ls, 1,N),
	apartDemo(Ls, Apart),
	fd_labeling(Ls).
	
apartBenchmark(N, Apart) :- 
	findall(X, apartDemoMain(2,X,Apart), [R]), R == [1,2],
	findall(_, apartDemoMain(N,_,Apart), [_]), !.

apartBenchmark(_, A) :- 
	write('that '), write(A), write('/3 predicate does not seem to work - sorry!'), nl.

% TODO: define 3 (or 4) versions of the following apart/3 constraint, and compare 
%       their respective performance with apartBenchmark/2

%--- apart(#X, #Y, #N) : X and Y are separated by an offset of at least N

    my_apart_v1(X,Y,N) :- X #< Y, X+N #=< Y.
    my_apart_v1(X,Y,N) :- X #> Y, Y+N #=< X.

    my_apart_v2(X,Y,N) :- (X+N #=< Y) #\/ (Y+N #=< X).

    my_apart_v3(X,Y,N) :- (X-Y)*(X-Y) #>= N*N.

%--- abc(A, B, C):
% 9A+8B+4C=0, with A,B,C with range [-5 .. +5] with minimal ecart of 3 between them
abc(A,B,C) :-
    fd_domain([A1,B1,C1], 0, 10),
    9*A1+8*B1+4*C1 #= 105,
    my_apart_v2(A1, B1, 3),
    my_apart_v2(B1, C1, 3),
    my_apart_v2(C1, A1, 3),
    fd_labeling([A1,B1,C1]),
    A is A1-5,
    B is B1-5,
    C is C1-5.

% ------------------------------------------------------------
% Ex. 3 : Triplets de Pythagore
%         - Expliquer laquelle des versions 1 ou 2 est "generate-and-test"
%         - Ecrire la version 3
%         - Comparer les performances avec des questions
%           comme : benchmark(20, pyth02(500, Ls)).

%--- Version naïve 1
pyth01(N, Ls) :-
    Ls = [A,B,C], 
    fd_domain(Ls,1,N),
    fd_labeling(Ls),
	A*A + B*B #= C*C.

%--- Version naïve 2
pyth02(N, Ls) :- 
  Ls = [A,B,C], 
  fd_domain(Ls,1,N),
  A*A + B*B #= C*C,
  fd_labeling(Ls).
	
%--- Version 3 appliquant les "bonnes pratiques"
%      - variables auxiliaires (AA#=A*A ...) 
%      - contrainte redondante (B#>A et 2*BB#>CC)
%      - distribution first-fail (fd_labelingff)
%      - symétrie cassée, et ...
%      - ... solutions symétriques générées _après_ le labeling;
pyth03(N, Ls):-
    Ls = [A, B, C],
    fd_domain(Ls, 1, N),
    AA #= A*A,
    BB #= B*B,
    CC #= C*C,
    B #> A,
    2*BB #> CC,
    AA + BB #= CC,
    fd_labelingff(Ls).

%--- Version 4 : Approche axée sur les triplets de Pythagore primitifs
%    (pour les intéressés, cf. http://en.wikipedia.org/wiki/Pythagorean_triple)
pyth04(N,Ls1) :- 
    Ls1 = [A1,B1,C],
    Ls = [U,V,K, A,B,C], 
    fd_domain(Ls,1,N),
	A*A + B*B #= C*C,
	UU #= U*U, 
	VV #= V*V,
	(U-V) rem 2 #= 1,
	coprime(U,V, Aux),
	A #= K*(UU - VV),
	C #= K*(UU + VV),
	B #= K*(2*U*V),
	append([UU, VV|Ls], Aux, AllVariables),
	fd_labelingff(AllVariables), 
	unbreak_symmetry(A,B, A1,B1).

%--- coprime(#A, #B, -ListOfAuxiliaryVarsFD): pgdc(A,B)==1
coprime(A,B, [X,Y]) :-
	X#=<B, Y#=<A, 
	(A*X - B*Y #= 1).

unbreak_symmetry(A,B, A,B).
unbreak_symmetry(A,B, B,A).

% ------------------------------------------------------------
% ---- Utility
% ------------------------------------------------------------
% ---- benchmark(HowManyTimes, Goal): measures duration to solve Goal

benchmark(N, P) :-
    statistics(real_time, _),
    benchmark(N, P, S),
    statistics(real_time, [_, Duration]),
    length(S,NbSol),
    write('there are '),write(NbSol), write(' solutions'), nl,
    MicroSecondsPerCall is Duration*1000//N,
    write('Avg time [us]: '), write(MicroSecondsPerCall), nl,
    fail.
benchmark(0, _, _).
benchmark(N, P, S) :- 
    N>0, N1 is N-1,
    findall(a, P, S), 
    benchmark(N1, P, _).

% ------------------------------------------------------------
% Ex. 4 : Trois fractions...
%         Vous devez "plonger" l'énoncé du problème dans le
%         domaine des entiers positifs; ce n'est pas compliqué…


toDo4.  % TODO 

% ------------------------------------------------------------
% Ex. 5 : Problème d'optimisation sur nombres réels...
%         Vous devez "plonger" l'énoncé du problème dans le
%         domaine des entiers positifs. Il y a typiquement 
%         deux approches possibles.
% Maximize z = 5x + 4y
  %6x + 4y <= 24
  %x + 2y <= 6
  %-x + y <= 1
  %y <= 2
  %x, y >= 0
  %Solution: x=3; y=1.5


toDo5(LD):-
    LD1 = [X,Y],
    fd_domain(LD1, 0, 4000),
    6*X + 4*Y #=< 24 * 1000,
    X + 2*Y #=< 6 * 1000,
    Y #=< 1 + X,
    Z #= 5*X + 4*Y,
    fd_maximize(fd_labeling(LD1), Z),
    X1 is X/1000,
    Y1 is Y/1000,
    LD = [X1,Y1].




% ------------------------------------------------------------
% Ex. 6 : Attributions des projets de semestre : cf proj_exo.pl

toDo6. % TODO



% ------------------------------------------------------------
% Ex. 7 : N-Queens problem, using a "matrix of boolean unknowns"

:- include('matrix.pl').
:- include('fd_utils.pl').

toDo7(N):-
    matrix_create(N, N, M),
    matrix_values(M, V),
    fd_domain_bool(V),
    fd_exactly(N, V, 1),
    matrix_transpose(M, Mt),
    exact_once_per_sublist(M),
    exact_once_per_sublist(Mt),
    matrix_diagonals(M, Md),
    max_once_per_sublist(Md),
    fd_labeling(V,[variable_method(random),value_method(max)]),
    matrix_show(M).

max_once_per_sublist([]).
max_once_per_sublist([M|Ms]):-
    fd_atmost(1, M, 1),
    max_once_per_sublist(Ms).

exact_once_per_sublist([]).
exact_once_per_sublist([M|Ms]):-
    fd_exactly(1, M, 1),
    exact_once_per_sublist(Ms).



