%--- An RxC matrix is represented with [ [E00, E01, E02...E0C],
%                                        [E10, E11, E12...E1C], 
%                                   ...  [ER0, ER1, ER2...ERC] ]
%
%--- matrix_create       (+NbOfRows, +NbOfCols, -Matrix)
%--- matrix_element      (+Matrix, ?I, ?J, ?Elt)
%--- matrix_values       (+Matrix, ?AllValues)
%--- matrix_transpose    (+Matrix, ?MatrixTransposed)
%--- matrix_diagonals    (+Matrix, ?ListOfDiagonals)
%--- matrix_big_diagonals(+Matrix, ?ListOfTwoDiagonals)
%--- matrix_dimensions   (+Matrix, ?NbOfRows, ?NbOfCols)
%--- matrix_replace      (+Matrix, +I, +J, +NewElt, ?Matrix1)
%--- matrix_submatrix    (+M, +FromI, +FromJ, Rows, Cols, ?Submatrix)
%--- matrix_show         (+M)    % grid-like, with no separator
%--- matrix_show_as_lists(+M)    % new-line after each row/list
%----------------------------------------------------------------------

matrix_show([]).
matrix_show([L|Ls]) :- m_showLine(L), nl, matrix_show(Ls).

m_showLine([]).
m_showLine([A|As]) :- write(A), m_showLine(As).


matrix_show_as_lists([]).
matrix_show_as_lists([L|Ls]) :- write(L), nl, matrix_show_as_lists(Ls).

%----------------------------------------------------------------------
matrix_submatrix(M, I, J, Rows, Cols, S) :-
		msm(M, S, Rows, Cols, I, J).
	
msm(_,[],0,_,_,_).
msm(M,[L|Ls],R,C,I,J) :- R>0,
		nth(I, M, Line),
		msublist(Line, J, C, L),
		R1 is R-1, I1 is I+1,
		msm(M, Ls, R1, C, I1, J).

msmtest(R) :- matrix_submatrix([[1,2,3],[4,5,6],[7,8,9],[a,b,c]], 1, 1, 1,1, R).

msublist(_,_,0,[]).
msublist(Ls, From, Len, [R|Rs]) :- Len>0,
        nth(From,Ls, R),
        From1 is From+1, Len1 is Len-1,
        msublist(Ls, From1, Len1,Rs).
%----------------------------------------------------------------------
m_diag(M, I, J, JInc, R, C, [V|Ds]) :- I>=1, J>=1, I=<R, J=<C, !,
                         % write(b(JInc)),nl,
        matrix_element(M, I, J, V),
        J1 is J+JInc, I1 is I+1,
        m_diag(M, I1, J1, JInc, R, C, Ds).

m_diag(_, _, _, _, _, _, []).

m_diag_enum(M, I, J, IInc, JInc, R, C, DJInc, [D|Ds], Ds1) :-
        I>=1, J>=1, I=<R, J=<C, !,
                        % write(a(DJInc)),nl,
        I1 is I+IInc, J1 is J+JInc,
        m_diag(M, I, J, DJInc, R, C, D),
        m_diag_enum(M, I1, J1, IInc, JInc, R, C, DJInc, Ds, Ds1).

m_diag_enum(_, _, _, _, _, _, _, _, Ds, Ds).

matrix_diagonals(M, Ds) :-
        matrix_dimensions(M, R, C), 
        %--- growing in J : clockwise from [R,1]
        m_diag_enum(M, R, 1, -1, 0, R, C,  1, Ds, Ds1),
        m_diag_enum(M, 1, 2, 0, +1, R, C,  1, Ds1, Ds2),
        %--- decreasing in J : clockwise from [1,1]
        m_diag_enum(M, 1, 1, 0, +1, R, C, -1, Ds2, Ds3),
        m_diag_enum(M, 2, C, 1, 0, R, C, -1, Ds3, []).


%----------------------------------------------------------------------
matrix_test1 :- 
        matrix_create(4, 3, M), M = [ [a,b,c], 
                                      [d,e,f],
                                      [g,h,i],
                                      [k,l,m] ],
        %m_diag(M, 1, 3, -1, 3, 3, D),
        matrix_diagonals(M, D),
        write(D), nl,
        nl.
%-------------------------------
matrix_test :- 
        matrix_create(3, 3, M),
        matrix_element(M, 1,1, 5),
        matrix_element(M, 2,2, 8),
        write(M), nl,
        m_values(M, 1, Vs, Is, Js),
        write(Vs), nl,
        write(Is), nl,
        write(Js), nl,
        matrix_transpose(M, MT),
        write(MT), nl,
        matrix_big_diagonals(M, BD),
        write(BD), nl,
        matrix_diagonals(M, D),
        write(D), nl,
        nl.
%----------------------------------------------------------------------

matrix_big_diagonals(M, [Ds1, Ds2]) :-
        matrix_dimensions(M, N, N),
        m_big_diag(M, N, 1, Ds1, Ds2).

m_big_diag(_, N, I, [], []) :- I>N.
m_big_diag(M, N, I, [A|Ds1], [B|Ds2]) :- I=<N,
        matrix_element(M, I, I, A),
        I1 is N+1-I, I2 is I+1,
        matrix_element(M, I, I1, B),
        m_big_diag(M, N, I2, Ds1, Ds2).
        

%----------------------------------------------------------------------
matrix_transpose(M, MT) :-
        matrix_dimensions(M, R, C),
        matrix_create(C, R, MT),
        m_values(M, 1, Vs, Is, Js),
        m_transpose(Vs, Is, Js, MT).

m_transpose([], [], [], _).
m_transpose([V|Vs], [I|Is], [J|Js], MT) :-
        matrix_element(MT, J, I, V),
        m_transpose(Vs, Is, Js, MT).

%----------------------------------------------------------------------
matrix_dimensions([L|M], R, C) :- length([L|M], R), length(L, C).
%----------------------------------------------------------------------
matrix_element(A,I,J,X):- nth(I,A,L), nth(J,L,X).
%----------------------------------------------------------------------
matrix_values(M, Vs) :- m_values(M, 1, Vs, _Is, _Js).
m_values([], _, [], [], []).
m_values([R|Rs], I, Vs, Is, Js) :- 
        m_values(R, I, 1, Vs, Vs1, Is, Is1, Js, Js1), 
        I1 is I+1,
        m_values(Rs, I1, Vs1, Is1, Js1). 

m_values([], _I, _J, Vs, Vs, Is, Is, Js, Js).
m_values([E|Es], I, J, [E|Vs], Vs1, [I|Is], Is1, [J|Js], Js1) :- 
        J1 is J+1,
        m_values(Es, I, J1, Vs, Vs1, Is, Is1, Js, Js1).

%----------------------------------------------------------------------
matrix_create(0, _, []).
matrix_create(R, C, [Ls|M]) :- R>0,
        length(Ls, C), R1 is R-1,
        matrix_create(R1, C, M).

%----------------------------------------------------------------------
matrix_replace(M, I, J, E, M1) :-
        nth(I, M, L),
        replace_nth(L, J, E, L1),
        replace_nth(M, I, L1, M1).

replace_nth([_|Ls], 1, E, [E|Ls]).
replace_nth([L|Ls], N, E, [L|Ls1]) :-
        N1 is N-1, replace_nth(Ls, N1, E, Ls1).
       
%----------------------------------------------------------------------
