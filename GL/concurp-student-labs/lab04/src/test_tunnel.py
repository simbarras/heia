#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Copyright 2020, School of Engineering and Architecture of Fribourg
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
Test the bird simulation
"""

__author__ = "Simon Barras"
__date__ = "2021-12-03"
__version__ = "0.1.0"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


import os  # call the file

# Constants
SOURCE_PATH = ".\\lab04\\src"
FILE_NAME = "tunnel.py"
PYTHON = "C:/Users/simon/Envs/concurp/Scripts/python.exe"

# Global variables
g_nb_north = 29
g_cycle_north = 23
g_nb_south = 31
g_cycle_south = 19
g_limit = 5


# Move working directory
os.chdir(SOURCE_PATH)


# Run
def run(*args):
    command = f"{PYTHON} {FILE_NAME} {' '.join(args)}"
    print(command)
    result = os.system(command)
    print("exit code: {}".format(result))
    return 0 == result


# test if not int number of north car's argument is correctly interpreted
def test_not_int_cars_north():
    assert(not run("-N dix"))
    assert(not run("-N 10dix"))
    assert(not run("-N 10.5"))


# test if not int number of south car's argument is correctly interpreted
def test_not_int_cars_south():
    assert(not run("-S dix"))
    assert(not run("-S 10dix"))
    assert(not run("-S 10.5"))


# test if not int number of north cycle's argument is correctly interpreted
def test_not_int_cycle_north():
    assert(not run("-n dix"))
    assert(not run("-n 10dix"))
    assert(not run("-n 10.5"))


# test if not int number of south cycle's argument is correctly interpreted
def test_not_int_cycle_south():
    assert(not run("-s dix"))
    assert(not run("-s 10dix"))
    assert(not run("-s 10.5"))


# test if not int limit's argument is correctly interpreted
def test_not_int_limit():
    assert(not run("-L dix"))
    assert(not run("-L 10dix"))
    assert(not run("-L 10.5"))


# test bounds of the number of north car's argument
def test_bound_north_car():
    assert(not run("-N -1"))
    assert(run("-N 0"))
    assert(run("-N 1"))


# test bounds of the number of south car's argument
def test_bound_south_car():
    assert(not run("-S -1"))
    assert(run("-S 0"))
    assert(run("-S 1"))


# test bounds of the cycle of north car's argument
def test_bound_north_cycle():
    assert(not run("-n -1"))
    assert(not run("-n 0"))
    assert(run("-n 1"))


# test bounds of the cycle of south car's argument
def test_bound_south_cycle():
    assert(not run("-n -1"))
    assert(not run("-n 0"))
    assert(run("-n 1"))


# test bounds of the limit's argument
def test_bound_limit():
    assert(not run("-n -1"))
    assert(not run("-n 0"))
    assert(run("-n 1"))


# test different number of arguments
def test_nb_argument():
    assert(run())
    assert(run("-N 5"))
    assert(run("-N 5 -S 5"))
    assert(run("-N 5 -S 5 -n 5"))
    assert(run("-N 5 -S 5 -n 5 -s 5"))
    assert(run("-N 5 -S 5 -n 5 -s 5 -L 5"))
    assert(run("-N 5 -S 5 -n 5 -L 5"))
    assert(run("-N 5 -S 5 -n 5 -L 5 -s 5"))
    assert(run("-N 5 -S 5 -s 5"))
    assert(run("-N 5 -S 5 -s 5 -n 5"))
    assert(run("-N 5 -S 5 -s 5 -n 5 -L 5"))
    assert(run("-N 5 -S 5 -s 5 -L 5"))
    assert(run("-N 5 -S 5 -s 5 -L 5 -n 5"))
    assert(run("-N 5 -S 5 -L 5"))
    assert(run("-N 5 -S 5 -L 5 -n 5"))
    assert(run("-N 5 -S 5 -L 5 -n 5 -s 5"))
    assert(run("-N 5 -S 5 -L 5 -s 5"))
    assert(run("-N 5 -S 5 -L 5 -s 5 -n 5"))
    assert(run("-N 5 -n 5"))
    assert(run("-N 5 -n 5 -S 5"))
    assert(run("-N 5 -n 5 -S 5 -s 5"))
    assert(run("-N 5 -n 5 -S 5 -s 5 -L 5"))
    assert(run("-N 5 -n 5 -S 5 -L 5 -s 5"))
    assert(run("-N 5 -n 5 -s 5"))
    assert(run("-N 5 -n 5 -s 5 -S 5"))
    assert(run("-N 5 -n 5 -s 5 -S 5 -L 5"))
    assert(run("-N 5 -n 5 -s 5 -L 5"))
    assert(run("-N 5 -n 5 -s 5 -L 5 -S 5"))
    assert(run("-N 5 -n 5 -L 5"))
    assert(run("-N 5 -n 5 -L 5 -S 5"))
    assert(run("-N 5 -n 5 -L 5 -S 5 -s 5"))
    assert(run("-N 5 -n 5 -L 5 -s 5"))
    assert(run("-N 5 -n 5 -L 5 -s 5 -S 5"))
    assert(run("-N 5 -s 5"))
    assert(run("-N 5 -s 5 -S 5"))
    assert(run("-N 5 -s 5 -S 5 -n 5"))
    assert(run("-N 5 -s 5 -S 5 -n 5 -L 5"))
    assert(run("-N 5 -s 5 -S 5 -L 5"))
    assert(run("-N 5 -s 5 -S 5 -L 5 -n 5"))
    assert(run("-N 5 -s 5 -n 5"))
    assert(run("-N 5 -s 5 -n 5 -S 5"))
    assert(run("-N 5 -s 5 -n 5 -S 5 -L 5"))
    assert(run("-N 5 -s 5 -n 5 -L 5"))
    assert(run("-N 5 -s 5 -n 5 -L 5 -S 5"))
    assert(run("-N 5 -s 5 -L 5"))
    assert(run("-N 5 -s 5 -L 5 -S 5"))
    assert(run("-N 5 -s 5 -L 5 -S 5 -n 5"))
    assert(run("-N 5 -s 5 -L 5 -n 5"))
    assert(run("-N 5 -s 5 -L 5 -n 5 -S 5"))
    assert(run("-N 5 -L 5"))
    assert(run("-N 5 -L 5 -S 5"))
    assert(run("-N 5 -L 5 -S 5 -n 5"))
    assert(run("-N 5 -L 5 -S 5 -n 5 -s 5"))
    assert(run("-N 5 -L 5 -S 5 -s 5"))
    assert(run("-N 5 -L 5 -S 5 -s 5 -n 5"))
    assert(run("-N 5 -L 5 -n 5"))
    assert(run("-N 5 -L 5 -n 5 -S 5"))
    assert(run("-N 5 -L 5 -n 5 -S 5 -s 5"))
    assert(run("-N 5 -L 5 -n 5 -s 5 -S 5"))
    assert(run("-N 5 -L 5 -s 5"))
    assert(run("-N 5 -L 5 -s 5 -S 5"))
    assert(run("-N 5 -L 5 -s 5 -S 5 -n 5"))
    assert(run("-N 5 -L 5 -s 5 -n 5"))
    assert(run("-N 5 -L 5 -s 5 -n 5 -S 5"))


# test with big arguments
def test_big():
    assert(run("-N 1000 -S 1000 -n 1000 -s 1000 -L 1000"))


# the big boss of tests
def test_life():
    assert(run("-N 42", "-n 42", "-S 42", "-s 42", "-L 42"))


if __name__ == "__main__":
    test_life()
