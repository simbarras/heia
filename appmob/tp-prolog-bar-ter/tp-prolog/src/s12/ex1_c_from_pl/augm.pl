%--- Declarations for the "compiler" gplc
%--- (not meaningful for the interpreter gprolog)

:- foreign(first_occurrence(+string,+char,-positive)).
:- foreign(char_ascii(?char,?code)).  

:- foreign(my_square(+integer, ?positive)).
:- foreign(my_square2(-integer, +positive), [choice_size(0)]).



