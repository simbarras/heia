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


import os                           # call the file

# Constants
SOURCE_PATH = ".\\lab03\\out\\production\\Babybird"
FILE_NAME = "BabyBird"

# Global variables
g_nb_threads = 10
g_max_food = 7
g_nb_life = 5
g_hunting_rate = 50


# Move working directory
os.chdir(SOURCE_PATH)


# Run
def run(*args):
    command = "java {} {}".format(FILE_NAME, " ".join(args))
    print(command)
    result = os.system(command)
    print("exit code: {}".format(result))
    return 0 == result

# test if not int argument is correctly interpreted


def test_not_int():
    assert(not run("s", "5", "5", "80"))
    assert(not run("5", "s", "5", "80"))
    assert(not run("5", "5", "s", "80"))
    assert(not run("5", "5", "5", "huit"))


# test if negative argument is correctly interpreted
def test_negative():
    assert(not run("-5", "5", "5", "80"))
    assert(not run("5", "-5", "5", "80"))
    assert(not run("5", "5", "-5", "80"))
    assert(not run("5", "5", "5", "-80"))


# test out of bound arguments
def test_out_of_bound():
    assert(not run("0", "5", "5", "80"))
    assert(not run("5", "0", "5", "80"))
    assert(not run("5", "5", "0", "80"))
    assert(not run("5", "5", "5", "120"))


# test the bound of chicks values
def test_bound_chick():
    assert(not run("0", "5", "5", "80"))
    assert(not run("-1", "5", "5", "80"))
    assert(run("1", "5", "5", "80"))


# test bound of life cycle values
def test_bound_life_cycle():
    assert(not run("5", "0", "5", "80"))
    assert(not run("5", "-1", "5", "80"))
    assert(run("5", "1", "5", "80"))


# test bound of food capacity
def test_bound_tank_size():
    assert(not run("5", "5", "0", "80"))
    assert(not run("5", "5", "-1", "80"))
    assert(run("5", "5", "1", "80"))


# test bound of hunting rate
def test_bound_hunting_rate():
    assert(not run("5", "5", "5", "-1"))
    assert(run("5", "5", "5", "1"))
    assert(run("5", "5", "5", "99"))
    assert(run("5", "5", "5", "100"))
    assert(not run("5", "5", "5", "101"))


# test different number of arguments
def test_nb_argument():
    assert(run())
    assert(run("5"))
    assert(run("5", "5"))
    assert(run("5", "5", "5"))
    assert(run("5", "5", "5", "80"))
    assert(not run("5", "5", "5", "80", "5"))


# test with big arguments but witch classic tank size's argument
# ! /!\ lot of time
def test_big_but_tank_size():
    assert(run("100", "100", "5", "99"))


# test with big arguments but witch classic chick's argument
# ! /!\ lot of time
def test_big_but_chick():
    assert(run("5", "1000", "5", "99"))
    assert(run("5", "1000", "10000", "99"))


# test with big arguments but witch classic life cycle's argument
def test_big_but_life_cycle():
    assert(run("5", "5", "10000", "99"))
    assert(run("10000", "5", "10000", "99"))


# test with big arguments
# ! /!\ lot of time
def test_big_arguments():
    assert(run("1000", "1000", "1000000", "99"))


# the big boss of tests
def test_life():
    assert(run("42", "42", "42", "42"))


# test with hunting rate at 0
# ! /!\ Infinity loop
def run_infinity():
    assert(True)
    assert(run("5", "5", "5", "0"))
    assert(False)


# test with classic values
# store result in a file
def test_ok():
    assert("cd {}; java {}{} > lab03/log.txt".format(SOURCE_PATH,
           FILE_NAME, g_nb_threads, g_nb_life, g_max_food, g_hunting_rate))


if __name__ == "__main__":
    test_big_but_tank_size()