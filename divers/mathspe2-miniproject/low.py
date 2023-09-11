#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Copyright 2022, School of Engineering and Architecture of Fribourg
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


"""
RSA Algorithm
"""

__author__ = "Barras Simon & Terreaux Nicolas"
__date__ = "2022-05-02"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch & nicolas.terreaux@edu.hefr.ch"
__userid__ = "simon.barras & nicolas.terreaux"

import argparse
import numpy as np

N_DEF = 7288501
E_DEF = 3
M_DEF = 123

prime_number = []
args = []


def get_arguments():  # Creation and retrievement of argument values
    global args
    parser = argparse.ArgumentParser(description='Arguments for RSA algorithm')
    parser.add_argument("-N", type=int, default=N_DEF, help="N value of RSA algorithm")
    parser.add_argument("-E", type=int, default=E_DEF, help="E value of RSA algorithm")
    parser.add_argument("-M", type=int, default=M_DEF, help="Message value of RSA algorithm")
    args = parser.parse_args()  # save arguments to global variable


# Resolve diophantine equation

def diophantine(a, b):
    if b == 0:
        return 1, 0
    else:
        u, v = diophantine(b, a % b)
        return v, u - (a // b) * v


# Bigger common divisor
def gcd(a, b):
    if a == 0:
        return b
    return gcd(b % a, a)


# Module n of m on power d
def power_mod(m, d, n):
    # transform number to binary and sort to LSB in 0 position
    bin_number = bin(d)[-1:1:-1]
    last, transversal_sum = m, 1
    first = True
    for i in range(len(bin_number)):
        # Always power 2 except the first time
        if first:
            last = (last ** 1) % n
            first = False
        else:
            last = (last ** 2) % n
        if bin_number[i] == '1':  # add only if there is a 1
            transversal_sum = (transversal_sum * last) % n
    return transversal_sum


def inverse(a, n):
    return power_mod(a, n, 1)


# Check if a number is prime
def is_prim_number(x):
    for i in range(x // 2):
        if x % 2 == 0:
            return False
    return True


# Check if a number is prime
# /!\ Only try with known prime numbers
def is_prim_number_quick(x):
    for num in prime_number:
        if x == num:
            return True
        if x % num == 0:
            return False
    prime_number.append(x)
    return True


# Find next prime number
def next_prim_number(q=1):
    q += 1
    while not is_prim_number_quick(q):
        q += 1
    return q


# Find two prime numbers p and q such that p*q = n (bruteforce)
def find_p_and_q(n):
    p = next_prim_number()
    q = n // p
    while n % p != 0 or not is_prim_number(q) or p == q:
        p = next_prim_number(p)
        q = n // p
        if p > q:
            raise Exception("No value found")
    return p, q


# Encrypt message
def encrypt(m, e, n):
    c = power_mod(m, e, n)
    return c


def get_key(n, e):
    # find p and q such that p and q are prime numbers and p*q = ni
    p, q = find_p_and_q(n)

    # compute phi(n) = (p-1)*(q-1)
    phi = (p - 1) * (q - 1)

    # find d such that d*e = 1 power_mod phi
    x, y = diophantine(e, phi)

    # Check if e * (x + phi) = 1 power_mod phi
    assert (e * (x + phi)) % phi == 1


    # Be sure that d is a positive value
    d = (x + phi) % phi
    return p, q, phi, d


# Decrypt message
def decrypt(c, d, n):
    m = power_mod(c, d, n)
    return m


# Sign message
def sign(m, d, n):
    s = power_mod(m, d, n)
    return s


def main():
    print(f"Start program with (N:{args.N}, E:{args.E}, M:{args.M})")
    print("Generating key")
    p, q, phi, d = get_key(args.N, args.E)
    print(f"p:\t{p}\nq:\t{q}\nphi:\t{phi}\nd:\t{d}")




    print("\nEncrypt")
    c = pow(args.M, args.E, args.N)
    print(f"E:\t{args.E}")
    print(f"M:\t{args.M}")
    print(f"N:\t{args.N}")
    print(f"{args.M} ^ {args.E} mod {args.N} = {c}")
    print(f"{args.M} ^ {args.E} = {c}")

    print(f"c:\t{c}")

    # Make the cube root of C
    print("\nDecrypt")
    m = int(np.cbrt(c))
    print(f"{c} ^ 1/3 = {m}")
    print(f"m:\t{m}")


if __name__ == '__main__':
    get_arguments()
    main()

'''
    prime_factors of n : p=5059 q=14407
    private_key : d=47'947'829
    encrypted : c=503'938
    signed : s=72'226'148

    pow(x, y, z) is equal to xy % z
'''
