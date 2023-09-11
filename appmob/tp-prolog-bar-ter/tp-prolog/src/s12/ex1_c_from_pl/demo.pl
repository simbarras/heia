%--- Mini-demo for : foreign(first_occurrence(+string,+char,-positive))
%                    foreign(char_ascii(?char,?code))

%--- To be consulted from the "augmented" interpreter
%--- 'augm' (compiled with gplc)

go:-  
    first_occurrence(abcdabcd, 'c', I), write(I), nl,
    char_ascii('A', 65),
    char_ascii('A', N), write(N), nl,
    char_ascii(C, 65),  write(C), nl.
