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
Test the printer simulation
"""

__author__ = "Simon Barras"
__date__ = "2021-12-21"
__version__ = "0.1.0"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


import os  # call the file

# Constants
SOURCE_PATH = "./lab05/src/"
FILE_NAME = SOURCE_PATH + "printerSimulation.py"
PYTHON = "python"


# Move working directory
# os.chdir(SOURCE_PATH)


# Run
def run(*args):
    command = f"{PYTHON} {FILE_NAME} {' '.join(args)}"
    print(command)
    result = os.system(command)
    print("exit code: {}".format(result))
    return 0 == result


# test if not int number of client who print on A is correctly interpreted
def test_not_int_client_A():
    assert(not run("--nbA dix"))
    assert(not run("--nbA 10dix"))
    assert(not run("--nbA 10.5"))


# test if not int number of client who print on B is correctly interpreted
def test_not_int_client_B():
    assert(not run("--nbB dix"))
    assert(not run("--nbB 10dix"))
    assert(not run("--nbAB 10.5"))
    

# test if not int number of client who print on A and B is correctly interpreted
def test_not_int_client_AB():
    assert(not run("--nbAB dix"))
    assert(not run("--nbAB 10dix"))
    assert(not run("--nbAB 10.5"))
    

# test if not int number of files is correctly interpreted
def test_not_int_files():
    assert(not run("--nb_files dix"))
    assert(not run("--nb_files 10.5"))
    assert(not run("--nb_files 10dix"))
    
    
# test if not int number of lines is correctly interpreted
def test_not_int_lines():
    assert(not run("--nb_lines dix"))
    assert(not run("--nb_lines 10.5"))
    assert(not run("--nb_lines 10dix"))


# test bounds of the number of client who print on A
def test_bound_client_A():
    assert(not run("--nbA -1"))
    assert(run("--nbA 0"))
    assert(run("--nbA 1"))


# test bounds of the number of client who print on B
def test_bound_client_B():
    assert(not run("--nbB -1"))
    assert(run("--nbB 0"))
    assert(run("--nbAB 1"))


# test bounds of the number of client who print on A and B
def test_bound_client_AB():
    assert(not run("--nbAB -1"))
    assert(run("--nbAB 0"))
    assert(run("--nbAB 1"))


# test bounds of the number of files
def test_bound_files():
    assert(not run("--nb_files -1"))
    assert(not run("--nb_files 0"))
    assert(run("--nb_files 1"))


# test bounds of the number of lines
def test_bound_lines():
    assert(not run("--nb_lines -1"))
    assert(not run("--nb_lines 0"))
    assert(run("--nb_lines 1"))
    
    
# test different number of arguments
def test_nb_argument():
    assert(run())
    assert(run("--nbA 10"))
    assert(run("--nbA 10 --nbB 10"))
    assert(run("--nbA 10 --nbB 10 --nbAB 10"))
    assert(run("--nbA 10 --nbB 10 --nbAB 10 --nb_files 10"))
    assert(run("--nbA 10 --nbB 10 --nbAB 10 --nb_files 10 --nb_lines 10"))
    assert(run("--nbB 10"))
    assert(run("--nbB 10 --nbAB 10"))
    assert(run("--nbB 10 --nbAB 10 --nb_files 10"))
    assert(run("--nbB 10 --nbAB 10 --nb_files 10 --nb_lines 10"))
    assert(run("--nbB 10 --nbAB 10 --nb_files 10 --nb_lines 10 --nbA 10"))
    assert(run("--nbAB 10"))
    assert(run("--nbAB 10 --nb_files 10"))
    assert(run("--nbAB 10 --nb_files 10 --nb_lines 10"))
    assert(run("--nbAB 10 --nb_files 10 --nb_lines 10 --nbA 10"))
    assert(run("--nbAB 10 --nb_files 10 --nb_lines 10 --nbA 10 --nbB 10"))
    assert(run("--nb_files 10"))
    assert(run("--nb_files 10 --nb_lines 10"))
    assert(run("--nb_files 10 --nb_lines 10 --nbA 10"))
    assert(run("--nb_files 10 --nb_lines 10 --nbA 10 --nbB 10"))
    assert(run("--nb_files 10 --nb_lines 10 --nbA 10 --nbB 10 --nbAB 10"))
    assert(run("--nb_lines 10"))
    assert(run("--nb_lines 10 --nbA 10"))
    assert(run("--nb_lines 10 --nbA 10 --nbB 10"))
    assert(run("--nb_lines 10 --nbA 10 --nbB 10 --nbAB 10 --nb_files 10"))


# test with big arguments
def test_big():
    assert(run("--nbA 1000 --nbB 1000 --nbAB 1000 --nb_files 1000 --nb_lines 1000"))


# the big boss of tests
def test_life():
    assert(run("--nbA 42 --nbB 42 --nbAB 42 --nb_files 42 --nb_lines 42"))


if __name__ == "__main__":
    test_life()
