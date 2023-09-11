% =========================================================================
% FILE     : bb.pl
% SUPPORT  : Bapst Frederic, HEIA-FR.
% CONTEXT  : Programmation Logique
% =========================================================================
% OVERVIEW : Blackboard engine
% =========================================================================

:-  op(650, xfx, '~~>').

%---------------------------------------------------------
%--- bb_run(+InitialBlackboard, -FinalBlackboard)
%---------------------------------------------------------
%bb_run(BB, BBFinal) :-
bb_run(BB, BBfinal):-
    write('bb: '),
    write(BB), nl,
    bb_filter(BB, Rules),
    bb_applySome(BB, Rules, BBfinal),
    write('bbfinal: '),
    write(BBfinal), nl.


%--- bb_applySome(+BB, +PossibleRules, -BBFinal) : 
%      BBFinal is the result when applying a rule randomly taken
%      from the list, then running the rest of the program

bb_applySome(BB, [], BB):-
    bb_db(BB). % no more possible rules, exit.
% bb_applySome(BB, Rules, BBFinal) :-  ...
bb_applySome(BB, Rules, NewBB):-
    length(Rules, Size),
    Size1 is Size + 1,
    random(1, Size1, N),
    nth(N, Rules, Rule),
    bb_applyRule(BB, Rule, BB1),
    !,
    bb_run(BB1, NewBB).


%--- bb_oneRule(+BB, -R) : R is a rule with IN data in blackboard,
%                          and valid Conditions
% bb_oneRule(BB, In/Cond~~>Body) :- ...
bb_oneRule(BB, In/Cond~~>Body) :-
    In/Cond~~>Body,
    bb_extract(BB, In, _),
    bb_solve(Cond).

%--- bb_filter(+BB, -Rs) : Rs contains every matching rule with respect to BB
% bb_filter(BB, Rs) :- ...
bb_filter(BB, Rs) :-
    findall(R, bb_oneRule(BB, R), Rs).

%--- bb_solve(+GoalList) : solves each goal in the list
bb_solve([]).
bb_solve([G|Gs]) :-
    G,
    bb_solve(Gs).

% ...

%---bb_applyRule(+BB, +Rule, -NewBB):  
%     NewBB is the blackboard after removing IN data from BB,
%     playing the actions, and adding OUT data into BB
% bb_applyRule(BB, In/_Cond~~>Action/Out, NewBB1) :-  ...
bb_applyRule(BB, In/Cond~~>Action/Out, NewBB1) :-
    bb_solve(Cond),
    bb_solve(Action),
    bb_extract(BB, In, BB1),
    append(BB1, Out, NewBB1).

%--- bb_extract(+BB, +InList, -NewBB) : NewBB is BB without In elements
%--- Example: bb_extract([a,b,c,d,e,f], [b,e], R) gives R = [a,c,d,f]
bb_extract(BB, [], BB).
bb_extract(BB, [X1|Xs], NewBB) :-
    select(X1, BB, BB1),
    bb_extract(BB1, Xs, NewBB).



%---bb_db(+BB) : current blackboard tracing
bb_db(BB) :- write(BB), nl.
% bb_db(BB) :- write(BB), nl, get_char(_).

