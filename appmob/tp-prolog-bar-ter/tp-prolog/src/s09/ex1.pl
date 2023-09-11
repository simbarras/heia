%=======================================================================
%   Sentence ::= NounPhrase VerbPhrase
% VerbPhrase ::= Verb [NounPhrase]
% NounPhrase ::= [Determiner] AdjNoun
%    AdjNoun ::= {Adjective} Noun
% Determiner ::= 'the'|'a'|...
%       Noun ::= 'cat'|'cats'|'mouse'|'mice'|...
%       Verb ::= 'eat'|'eats'|...
%  Adjective ::= 'red'|'pretty'|...

determiner( _Num)    --> [W], {member(W, [the, my])}.
determiner(singular) --> [W], {member(W, [a, this])}.
determiner(plural)   --> [W], {member(W, [those, many])}.

noun(singular)       --> [W], {member(W, [cat, mouse, dog])}.
noun(plural)         --> [W], {member(W, [cats, mice, dogs])}.

verb(plural)         --> [W], {member(W, [eat,  follow ])}.
verb(singular)       --> [W], {member(W, [eats, follows])}.

adjective            --> [W], {member(W, [red, pretty, old])}.

conjunction          --> [W], {member(W, [and])}.

richNoun(Num)        --> noun(Num).
richNoun(Num)        --> adjective, richNoun(Num).

sentence --> nounPhrase(Num), verbPhrase(Num).
sentence --> nounPhrase(Num), conjunction, nounPhraseGroup(_Num1), verbPhrase(plural).

nounPhrase(Num) --> determiner(Num), richNoun(Num).
nounPhrase(Num) --> richNoun(Num).

%Add a conjunction
nounPhraseGroup(Num) --> nounPhrase(Num).
nounPhraseGroup(Num) --> nounPhrase(Num), conjunction, nounPhraseGroup(_Num1).

verbPhrase(Num) --> verb(Num).
verbPhrase(Num) --> verb(Num), nounPhrase(_Num1).



%=======================================================================
ex1_test :-
        write('Trying some sentences...'), nl,
        sentence([the,red,cat,eats,a,mouse], []), %OK
        sentence([the,cats,eat,a,red,mouse], []), %Ok
        sentence([red,cats,eat,pretty,mice], []), %OK
        sentence([my,cats,and,the,dog,eat,the,pretty,mouse],[]), %OK
        sentence([my,dog,and,the,red,cat,eat,a,pretty,mouse],[]), %OK
        write('Trying several adjectives...'), nl,
        sentence([the,pretty,red,cat,eats,a,mouse], []), %OK
        write('Trying a wrong sentence...'), nl,        
     \+ sentence([my,dog,and,the,red,cat,eats,a,pretty,mouse],[]), %Ok
     	write('Test OK').
        