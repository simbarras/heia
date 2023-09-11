% =========================================================================
% OVERVIEW : State graphs. Forward chaining
% =========================================================================

%=======================================================================
%               ** Squelette d'automate [Sterling] **
%=======================================================================

solveDfs(State,_,[]) :- finalState(State).
solveDfs(State,History,[Move|Moves]) :-
    transition(State,Move,State1),
    %write(['transition ?',Move, State1]), nl,
    legalState(State1),
    write(['legal ?',Move, State1]), nl,
    \+  member(State1,History),
    write(['done',Move, State, State1]), nl,
    solveDfs(State1,[State1|History],Moves).

solveStateProblem(ProblemName,Moves) :-
    initialState(ProblemName,State),
    solveDfs(State,[State],Moves).

%=======================================================================
%               ** Idem avec parcours en largeur d'abord **
%=======================================================================

%--- solveBfs :- solve state problem with Breadth First Search
solveBfs(ProblemName, Moves) :- 
    initialState(ProblemName, InitialState),
    fifo_new(Queue), 
    InitialNode = n(InitialState, []),
    bfs(InitialNode, Queue, [], Moves).

%--- Fifo Queue will hold "exploration nodes" n(CrtState, PastMoves)

bfs(n(S,Moves), _, _, Moves1) :- 
    finalState(S),
    reverse(Moves, Moves1).   % Moves were pushed in reverse order...
		
bfs(n(S,Ms), Queue, History, Moves) :-
    findall(n(S1,[M|Ms]), transition(S,M,S1), Ls),
    enqueueRelevantNodes(Ls, History, Queue, Queue1), 
    fifo_apply(Queue1, dequeue(NextNode), Queue2),
    bfs(NextNode, Queue2, [S|History], Moves).

enqueueRelevantNodes([], _, Queue, Queue).

enqueueRelevantNodes([Node|Ls], History, Queue, Queue2) :- 
    Node = n(S,_Ms),
    legalState(S),
    \+ member(S, History),
    % (next rule should skip illegal or already visited states)
    !,
    fifo_apply(Queue, enqueue(Node), Queue1), 
    enqueueRelevantNodes(Ls, History, Queue1, Queue2).

enqueueRelevantNodes([_|Ls], History, Queue, Queue1) :-
    enqueueRelevantNodes(Ls, History, Queue, Queue1).

% ------------------------------------------------------------
% fifo : FIFO queue ADT; representation : with simple list
%    Operations :  new,           enqueue(+Elt),
%                  consult(?Elt), dequeue(?Elt)
fifo_new([]).
fifo_apply(Xs,     enqueue(A), Ys) :- append(Xs, [A], Ys).
fifo_apply([A|Xs], dequeue(A), Xs).
fifo_apply([A|Xs], consult(A), [A|Xs]). % isEmpty <=> not consult(_)
% ------------------------------------------------------------
