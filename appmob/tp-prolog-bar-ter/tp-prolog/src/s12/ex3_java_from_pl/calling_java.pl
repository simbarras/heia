%--- send a request, receive the answer
call_sqr(N, Result, ToJava, FromJava) :-
    write(ToJava, 'square'), nl(ToJava), flush_output(ToJava),
    write(ToJava, N),        nl(ToJava), flush_output(ToJava),
    read_token(FromJava, Result). % normally an integer, but if the Java process
    %  was not correctly launched we'll get the term punct(end_of_file)
        
%--- start process, open connection
start_java(ToJava, FromJava) :- 
    Cmd = 'javaw CalledFromProlog',  % TODO: change to 'java' if you're on Linux...
    exec(Cmd, ToJava, FromJava, _FromJavaErr).

%--- send exit request
close_java(ToJava, FromJava) :-
    write(ToJava, 'bye'), nl(ToJava), flush_output(ToJava),
    close(ToJava),
    close(FromJava).

%--- mini-demo main
go :- 
    start_java(Send, Receive),
    call_sqr(3, X, Send, Receive),
    write('output is '), write(X), nl,
    call_sqr(X, Y, Send, Receive),
    write('output is '), write(Y), nl,
    close_java(Send, Receive).
