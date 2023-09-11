/*-------------------------------------------------------------------------*
 * GNU Prolog                                                              *
 *                                                                         *
 * Part  : foreign facility test                                           *
 * File  : examp_c.c                                                       *
 * Descr.: test file - C part                                              *
 * Author: Daniel Diaz                                                     *
 *-------------------------------------------------------------------------*/

#include <string.h>
#include <math.h>
#include "gprolog.h"

/*--------- Constants           ---------*/
/*--------- Type Definitions    ---------*/
/*--------- Global Variables    ---------*/
/*--------- Function Prototypes ---------*/

/*-----------------------------------*
 * FIRST_OCCURRENCE                  *
 *-----------------------------------*/
PlBool first_occurrence(char *str, PlLong c, PlLong *pos) {
  char *p;
  p = strchr(str, c);
  if (p == NULL)      // C does not appear in A
    return PL_FALSE;  // fail
  *pos = p - str;     // set the output argument
  return PL_TRUE;     // succeed
}

/*-----------------------------------*
 * CHAR_ASCII                        *
 *-----------------------------------*/
PlBool char_ascii(PlFIOArg *c, PlFIOArg *ascii) {
  if (!c->is_var) {              // Char is not a variable 
    ascii->unify = PL_TRUE;      // enforce unif. of Code 
    ascii->value.l = c->value.l; // set Code 
    return PL_TRUE;              // succeed 
  }

  if (ascii->is_var)             // Code is also a variable 
    Pl_Err_Instantiation();      // emit instantiation_error

  c->value.l = ascii->value.l;   // set Char
  return PL_TRUE;                // succeed  
}

/*--------------------------------------------------------------------*
 * my_square(+N, ?Nsquare)                                            *
 *--------------------------------------------------------------------*/
PlBool my_square(PlLong n, PlFIOArg *n_square) {
  if (n_square->is_var){
    n_square->value.l = n * n;
    return PL_TRUE;
  }
  if ((n * n) == n_square->value.l){
      return PL_TRUE;
  } else {
      return PL_FALSE;
  }
}

/*--------------------------------------------------------------------*
 * my_square2(-N, +Nsquare)
 *--------------------------------------------------------------------*/
PlBool my_square2(PlLong *n, PlLong n_square) {
    if(Get_Choice_Counter() == 0) {
        *n = (PlLong) sqrt((double) n_square);
        return PL_TRUE;
    }
    if(Get_Choice_Counter() == 1) {
        *n = - *n;
        return PL_TRUE;
    }
    No_More_Choice();
    return PL_FALSE;
}

