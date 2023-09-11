% =========================================================================
% FILE     : bst.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Techniques Avancées de Programmation 3 : Programmation Logique
% =========================================================================
% OVERVIEW : Abstract Data Types in Prolog - Binary Search Trees
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
bst_test :-
        bst_new(Bst),
        Cmds = [put(10,1), 
                put(20,2), 
                put(15,9),
                get(10,A), 
                put(30,3),
                put(25,8), 
                remove(40),
                remove(20), 
                get(30,B),
                get(15,C),
                get(25,D)],
        adt_test_apply(bst_apply, Bst, Cmds, Bst1),
     \+ bst_apply(Bst1, get(40,_),_),
     \+ bst_apply(Bst1, get(20,_),_),
        assertEquals([1,3,9,8], [A,B,C,D]),
        write('Test passed successfully.').

assertEquals(Expected, Effective) :-
	Expected==Effective.
assertEquals(Expected, Effective) :-
	Expected\==Effective,
	write('bad news... Expected: '), write(Expected),
	write(' Effective: '), write(Effective), nl, 
	fail.



% ------------------------------------------------------------
% bst : Map (dictionary) ADT (with numbers as keys)
%    Operations :
%      new(-X),      put(+Key, +Value),
%      remove(+Key), get(+Key, -Value),  
%    Representation : with binary search tree
%      nil, or n(Key, Value, LeftBST, RightBST)

bst_new(nil).

bst_apply(n(K,_,L,R),   put(K,Val), n(K,Val,L,R)).
bst_apply(nil,          put(K,Val), n(K,Val,nil,nil)).
bst_apply(n(K1,V,L,R),  put(K,Val), n(K1,V,L1,R)) :- K < K1,
        bst_apply(L, put(K,Val), L1).
bst_apply(n(K1,V,L,R),  put(K,Val), n(K1,V,L,R1)) :- K > K1,
        bst_apply(R, put(K,Val), R1).

% bst_apply(???,          put(K,Val), ???         ) :- K > K1,
%         ???

% --- bst_apply(???,   remove(K), ???)...
%    5 rules :
%       - empty tree
%       - reached node without right child
%       - reached node with right child (needs left rotation)
%       - current node is smaller
%       - current node is greater

% empty tree
bst_apply(nil, remove(_), nil).

% reached node without right child
bst_apply(n(K,_,L,nil), remove(K), L).

% reached node with right child (needs left rotation)
bst_apply(n(K,V,L,n(KR1,VR1,LR1,RR1)),remove(K),n(KR1,VR1,LR2,RR1)):-
    bst_apply(n(K,V,L,LR1),remove(K),LR2).

% current node is smaller
bst_apply(n(K,V,L,R), remove(K2), n(K,V,L1,R)) :-
    K > K2,
    bst_apply(L, remove(K2), L1).

% current node is greater
bst_apply(n(K,V,L,R), remove(K2), n(K,V,L,R1)) :-
    K < K2,
    bst_apply(R, remove(K2), R1).

% --- bst_apply(???,   get(K,Val), ???)...
bst_apply(n(K,Val,L,R), get(K,Val), n(K,Val,L,R)).
bst_apply(n(K1,V1,_,R), get(K,Val), n(K1,V1,_,R)) :-
    K1 < K,
    bst_apply(R, get(K,Val), _).
bst_apply(n(K1,V1,L,_), get(K,Val), n(K1,V1,L,_)) :-
    K1 > K,
    bst_apply(L, get(K,Val), _).


