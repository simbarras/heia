% ------------------------------------------------------------
% --- fdu_scalarProd(#As, #Bs, #Z) 

fdu_scalarProd(As, Bs, Z) :- fdu_scalarProd(As, Bs, 0, Z).
fdu_scalarProd([],[],Z,Z).
fdu_scalarProd([A|As], [B|Bs], N, Z) :-
    N1 #= N+A*B,
    fdu_scalarProd(As, Bs, N1, Z).

% ------------------------------------------------------------
% --- fdu_listSum(#Ls, #Sum)

fdu_listSum(Ls, S) :- fdu_listSum(Ls, 0, S).
fdu_listSum([], S, S).
fdu_listSum([E|Ls], N, S) :-
    N1 #= N+E,
    fdu_listSum(Ls, N1, S).
