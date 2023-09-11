% No need to define a "main" to read requests and send results :
% the Prolog interpreter itself does the job, but 
% the caller then has to know how to decode the results...

mySquare(N, M) :- M is N*N.
