
%=======================================================================
% WordNumber ::= 'zero' | Xxx
%        Xxx ::= (Digit 'hundred' RestXxx) | Xx
%    RestXxx ::= [ 'and' Xx ]
%         Xx ::= Digit | Teen | ( Tens RestXx )
%     RestXx ::= [ Digit ]
%      Digit ::= 'one'|'two'  |'three'|'four'|'five'|
%                'six'|'seven'|'eight'|'nine'
%       Teen ::= 'ten'|'eleven'|'twelve'| ... |'nineteen'
%       Tens ::= 'twenty'|'thirty'|       ... |'ninety'
%
% In EBNF, [...] means "optional"

digit(1) --> [one].       digit(2) --> [two].       digit(3) --> [three].
digit(4) --> [four].      digit(5) --> [five].      digit(6) --> [six].
digit(7) --> [seven].     digit(8) --> [eight].     digit(9) --> [nine].      

teen(11) --> [eleven].    teen(12) --> [twelve].    teen(13) --> [thirteen].
teen(14) --> [fourteen].  teen(15) --> [fifteen].   teen(16) --> [sixteen].
teen(17) --> [seventeen]. teen(18) --> [eighteen].  teen(19) --> [nineteen].
teen(10) --> [ten].       

tens(20) --> [twenty].    tens(30) --> [thirty].
tens(40) --> [fourty].    tens(50) --> [fifty].     tens(60) --> [sixty].
tens(70) --> [seventy].   tens(80) --> [eighty].    tens(90) --> [ninety].



% TODO - A COMPLETER

% wordNumber(...) --> ...
wordNumber(N) --> [zero], {N is 0}.
wordNumber(N) --> hundred(N).
wordNumber(N) --> xx(N).

hundred(N) --> digit(D), [hundred], restHundred(N, D).
restHundred(N, D) --> [and], xx(N1), {N is N1 + D*100}.
restHundred(N, D) --> {N is D*100}.

xx(N) --> digit(N).
xx(N) --> teen(N).
xx(N) --> tens(N1), restXx(N, N1).

restXx(N, N1) --> digit(N2), {N is N1 + N2}.
restXx(N, N1) --> {N is N1}.


% ...

%=======================================================================
ex4_test :-
        write('Trying some number sentences...'), nl, 
        wordNumber(275, [two, hundred, and, seventy, five], []),
        wordNumber(200, [two, hundred], []),
        wordNumber(20,  [twenty], []),
        write('Trying a wrong number sentence...'), nl, 
     \+ wordNumber(_,   [two, hundred, fifteen, seven], []),
        write('Trying to count all number sentences...'), nl, 
        findall(R, wordNumber(_,_,[]), Rs), length(Rs, Len),
        write(Len), write(' accepted numbers, should be 1000'), nl, 
        Len == 1000,
        write('Test OK'), nl.

