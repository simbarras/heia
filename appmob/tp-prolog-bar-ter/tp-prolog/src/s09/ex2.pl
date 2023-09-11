%=======================================================================
%   Sentence ::= NounPhrase VerbPhrase
% VerbPhrase ::= Verb [NounPhrase]
% NounPhrase ::= [Determiner] Noun
% Determiner ::= 'the'|'a'|...
%       Noun ::= 'cat'|'cats'|'mouse'|'mice'|...
%       Verb ::= 'eat'|'eats'|...
%  Adjective ::= 'red'|'pretty'|...

% TODO - A ADAPTER

determiner(W, _Num)    --> [W], {member(W, [the, my])}.
determiner(W, singular) --> [W], {member(W, [a, this])}.
determiner(W, plural)   --> [W], {member(W, [those, many])}.

noun(W, singular)       --> [W], {member(W, [cat, mouse, dog])}.
noun(W, plural)         --> [W], {member(W, [cats, mice, dogs])}.

verb(W, plural)         --> [W], {member(W, [eat,  follow ])}.
verb(W, singular)       --> [W], {member(W, [eats, follows])}.

sentence(s(NounPhraseStruct, VerbStruct)) --> nounPhrase(Num, NounPhraseStruct), verbPhrase(Num, VerbStruct).

nounPhrase(Num, np(DetStruct, NounStruct)) --> determiner(D, Num), noun(N, Num), {NounStruct=noun(N), DetStruct=det(D)}.
nounPhrase(Num, np(NounStruct)) --> noun(N, Num), {NounStruct=noun(N)}.

verbPhrase(Num, vp(VerbStruct)) --> verb(V, Num), {VerbStruct = verb(V)}.
verbPhrase(Num, vp(VerbStruct, NounPhraseStruct)) --> verb(V, Num), nounPhrase(_Num1, NounPhraseStruct), {VerbStruct = verb(V)}.

%===================
s(NounPhraseStruct, VerbStruct).

np(DetStruct, NounStruct).
np(NounStruct).

vp(Verb).
vp(Verb, NounPhraseStruct).

det(Det).

%=======================================================================
ex2_test :-
        sentence(X, [the,cat,eats,a,mouse],[]), 
        Y = s( np(det(the),noun(cat)), vp(verb(eats), np(det(a), noun(mouse)) )),
        checkOk(X,Y),
        write('Test passed successfully'),nl.
checkOk(X,Y) :- X==Y, !.
checkOk(X,Y) :- write('Observed: '), write(X), nl, write('Expected: '), write(Y), nl, fail.


