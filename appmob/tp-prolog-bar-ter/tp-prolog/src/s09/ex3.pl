% ------------------------------------------------------------

determiner(_Num)     --> [W], {member(W, [the, my])}.
determiner(singular) --> [W], {member(W, [a, this])}.
determiner(plural)   --> [W], {member(W, [those, many])}.

noun(singular)  --> [W], {member(W, [cat, mouse, dog] )}.
noun(plural)    --> [W], {member(W, [cats, mice, dogs])}.

verb(W, plural)   --> [W], {allVerbs(Vs), member([W, _], Vs)}.
verb(V, singular) --> [W], {allVerbs(Vs), member([V, W], Vs)}.

allVerbs(Ds) :- Ds = [
     %--[InfinitiveOrPlural, SingularThirdPerson]
		[               eat, eats               ],
		[            follow, follows            ],
		[             catch, catches            ]
	]. 

nounPhrase(Num) --> determiner(_Num), noun(Num).
nounPhrase(Num) --> noun(Num).

verbPhrase([V, NumO], Num) --> verb(V, Num), nounPhrase(NumO).
verbPhrase([V],       Num) --> verb(V, Num).

sentence([NumS|Vs]) --> nounPhrase(NumS), verbPhrase(Vs, NumS).

% ------------------------------------------------------------
pronoun(singular, _Role) --> [it].
pronoun(plural, subject) --> [they].
pronoun(plural, object)  --> [them].

auxiliary(singular) --> [does].
auxiliary(plural)   --> [do].

% pronominalQuestion(...) --> ...    TODO


pronominalQuestion([NumS|Vs]) --> auxiliary(NumS), pronoun(NumS, subject), infWithPronoun(Vs).
pronominalQuestion([NumS|Vs]) --> auxiliary(NumS), pronoun(NumS, subject), verb(Vs, plural).
infWithPronoun([Vs|Nums]) --> verb(Vs, plural), pronoun(NumS, object).


%---------------------------------------
questionFromSentence(Sentence, Question) :-
	sentence(S, Sentence, []),
	pronominalQuestion(S, Question, []).

%---------------------------------------
ex3_test :-
	Sentences=[
		[the,cat,eats,a,mouse], 
		[cats,follow,mice], 
		[a,cat,eats,mice],
		[my,cats,eat,the,mouse],
		[my,cats,eat],
		[a,cat,eat,a,cat]
	],
	qfs(Sentences),
	findall(_, pronominalQuestion(_,_,[]), Rs), length(Rs, Len),
	write(Len), write(' accepted pronominal questions, expected 18'), nl,
	Len==18,
	write('Test passed successfully'), nl.

qfs([]).
qfs([S|Ss]) :- 
	write(S), 
	questionFromSentence(S, Q),
	write('\t'), write(Q), nl,
	!,
	qfs(Ss).
	
qfs([_|Ss]) :-
	write('\tis not considered valid...'), nl,
	qfs(Ss).
