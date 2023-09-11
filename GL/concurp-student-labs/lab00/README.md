# Lab00 - Environment setup and some debugging

In the the directories you find the corresponding code snippets for the debugging and beautifying exercise. For beautifying, refer to the [10 coding commandments](https://concurp.pages.forge.hefr.ch/resources/ten_coding_cmdt/)

## The C program
This program should multiply all the values give as arguments.

### Examples
If you launch `lab00-multiplier 1 3 5 7` then you should get 105

If you launch `lab00-multiplier 2 3 4 5 6` then you should get 720

### How to compile
To easily compile the example code, you have a CMake file. Using this CMake file, you create a Makefile with finally compiles your code. You can also use directly the compiler `gcc`

```bash
cmake .
make
./lab00-multiplier 1 3 5 7   # this will execute the program
```

---

## The Python program
In the corresponding directory you will find a python script that calculates the roots of a quadratic equation. 

```math
ax^2+bx+c=0
```
The script asks for the paramters a, b, and c. And the tries to calculate the roots, if there exists any. After each calculation, the script should print the solution at the screen. It should look like this:

```bash
$ python quadratic_solver_solution.py
a: 1
b: 1
c: -1
(0.6180339887498949, -1.618033988749895)
a:
```

The program has some bugs and doesn't work as expected. As well, the 10 commandments are not respected. So, fix these issues with the help of the debugger in your IDE and write the different steps into multiple commits that you must upload to the remote repository at the end.

### Hint for the quadratic equation
On the this [wiki page](https://en.wikipedia.org/wiki/Quadratic_formula#Method_2) you find some help. The implemented method is based on the using the *completing square method*, which you might konw very well :grinning:


