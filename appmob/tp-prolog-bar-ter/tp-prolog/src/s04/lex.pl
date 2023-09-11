%   NOT TO BE STUDIED !

% =========================================================================
% FILE     : lex.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : DCG Prolog, arithmetic expressions interpretation
%            Lexical analyzer. Example :     "(32*(4-1)+sqr(5))/2"   gives :
%            Tokens = [special('('),number(32),special(*),special('('),
%                      number(4),special(-),number(1),special(')'),
%                      special(+),ident(sqr),special('('),number(5),
%                      special(')'),special(')'),special(/),number(2)]
% =========================================================================

% ------------------------------------------------------------
lexicalAnalyser(FileName, Tokens) :-
        open(FileName, read, F),
        get_char(F, C),
        tk(F, C, Tokens).
la(F, T) :- lexicalAnalyser(F, T).

tk(F, end_of_file, []) :-    !, close(F).
tk(F, C, Ts) :- isSpace(C),  !, get_char(F, C1), tk(F, C1, Ts).
tk(F, C, Ts) :- isDigit(C),  !, readNumber(F, C, N), 
        get_char(F, C1), Ts=[number(N)|Ts1], tk(F, C1, Ts1).
tk(F, C, Ts) :- isLetter(C), !, readIdent(F, C, I),
        get_char(F, C1), Ts=[ident(I)|Ts1], tk(F, C1, Ts1).
tk(F, C, Ts) :- %--- otherwise
        get_char(F, C1), Ts=[special(C)|Ts1], tk(F, C1, Ts1).

isDigit(C)  :- C @>= '0', C @=< '9'.
isLetter(C) :- C @>= 'a', C @=< 'z'.
isSpace(C)  :- member(C, [' ', '\n']).

readNumber(F, C, N) :- readNb(F, C, Ls), number_chars(N,Ls).
readNb(_, end_of_file, []) :- !.
readNb(F, C, [C|Ls]) :- isDigit(C), !, 
        get_char(F, C1), readNb(F, C1, Ls).
readNb(F, C, []) :- unget_char(F, C).  %--- otherwise

readIdent(F, C, I) :- readId(F, C, Ls), atom_chars(I, Ls).
readId(_, end_of_file, []) :- !.
readId(F, C, [C|Ls]) :- isLetter(C), !, 
        get_char(F, C1), readId(F, C1, Ls).
readId(F, C, []) :- unget_char(F, C).  %--- otherwise




