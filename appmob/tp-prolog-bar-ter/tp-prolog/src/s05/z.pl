%--- z(+ListOfAtoms, ?Result)
z(Ls, Cs) :- z(Ls, [ [] ], Cs).

z(Ls, Ts, [I,B|Cs]) :-
	nth9(I, Ts, As),
	append(As, [B], Asb), 
	append(Asb, Bs, Ls),
	!,
	z(Bs, [Asb|Ts], Cs).

z([], _, []).
% ------------------------------------------------------------
%--- nth9(?Index, ?List, ?Elt)
nth9(I,Ls,T) :- 
    nth(M,Ls,T), 
    length(Ls, N), 
    I is N+1-M.

% ------------------------------------------------------------
% ----    | ?- z([n,a,b,a,b],Zs).
% ----         Zs = [1,n,1,a,1,b,3,b]

