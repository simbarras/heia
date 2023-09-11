//
// -*- coding: utf-8 -*-

// Copyright 2021, School of Engineering and Architecture of Fribourg
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
 *  A very easy program who multiply some values
*/

/*
 * Author   : Simon Barras
 * Date     : 2021-09-21
 * Version  : 0.2
 * email    : simon.barras@edu.hefr.ch
 * userid   : simon.barras
*/

#include <stdio.h>
#include <stdlib.h>

int main(int nbOfArgs, char *args[])
{
    // check if at least one argument
    if (nbOfArgs == 1)
    {
        printf("Result: At least one argument required");
        return 0;
    }

    // initialize variables
    const int MAX_ARGS = 4;
    int product = 1;

    // convert the arguments into a int array
    for (int i = 0; i < MAX_ARGS; i++)
    {
        if (nbOfArgs > i)
            product = product * atoi(args[i + 1]); // the first argument is the prg name
        else
            break;
    }

    printf("Result: The multiplied output of this super program is: %d\nHope you had fun with this program and will use it a lot ;-)\n", product);
    return 0;
}
