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
__date__ = "2021-11-12"
__version__ = "0.1.0"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


import os                           # call the file
import matplotlib.pyplot as plt     # Draw graphics

# Constants
PYTHON_PATH = "C:/Users/simon/Envs/concurp/Scripts/python.exe"
GRAPH_COLORS = {
    "sleeping": "deepskyblue",
    "filling": "darkviolet",
    "eating": "red",
    "digesting": "yellow",
    "ready": "black",
    "hunting": "darkorange",
    "throwing": "forestgreen",
    "taking": "blue",
    "dying": "black",
    "buried": "grey",
}

# Global variables
g_nb_threads = 10
g_max_food = 7
g_nb_life = 5
g_hunting_rate = 50


# test if not int argument is correctly interpreted
def test_not_int():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py s 5 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 s 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 s 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 huit'))


# test if negative argument is correctly interpreted
def test_negative():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py -5 5 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 -5 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 -5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 -80'))


# test out of bound arguments
def test_out_of_bound():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 0 5 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 0 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 0 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 120'))


# test the bound of chicks values
def test_bound_chick():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 0 5 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py -1 5 5 80'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 1 5 5 80'))


# test bound of life cycle values
def test_bound_life_cycle():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 0 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 -1 5 80'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 1 5 80'))


# test bound of food capacity
def test_bound_tank_size():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 0 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 -1 80'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 1 80'))


# test bound of hunting rate
def test_bound_hunting_rate():
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 -1'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 1'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 99'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 100'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 101'))


# test different number of arguments
def test_nb_argument():
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 80'))
    assert(not os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 80 5'))


# test with big arguments but witch classic tank size's argument
# ! /!\ lot of time
def test_big_but_tank_size():
    #assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 1000 5 5 99'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 100 100 5 99'))


# test with big arguments but witch classic chick's argument
# ! /!\ lot of time
def test_big_but_chick():
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 1000 5 99'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 1000 10000 99'))


# test with big arguments but witch classic life cycle's argument
def test_big_but_life_cycle():
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 10000 99'))
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 10000 5 10000 99'))


# test with big arguments
# ! /!\ lot of time
def test_big_arguments():
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 1000 1000 100000 99'))


# the big boss of tests
def test_life():
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py 42 42 42 42'))


# test with hunting rate at 0
# ! /!\ Infinity loop
def run_infinity():
    assert(True)
    os.system(PYTHON_PATH + ' lab02/src/babybird.py 5 5 5 0')
    assert(False)


# test with classic values
# store result in a file
def test_ok():
    assert(os.system(PYTHON_PATH + ' lab02/src/babybird.py ' +
                     str(g_nb_threads) + ' ' + str(g_nb_life) +
                     ' ' + str(g_max_food) + ' ' + str(g_hunting_rate) +
                     ' > lab02/src/log.txt'))


# display the graph of the "test_ok()"
def show_graph():
    # Build graph
    fig, (ax1, ax2, ax3) = plt.subplots(3, 1, sharex=True)
    fig.suptitle("babybird test")
    ax1.set_ylabel('Thread')
    ax2.set_ylabel('Worm')
    ax3.set_xlabel('Time')
    ax3.set_ylabel('Chick')

    # Initialize value
    global g_nb_threads
    nest_food = 0           # number of food in the nest
    last_x = 0              # last x occurrence
    last_eat = 0            # last x occurrence when a chicks eat
    nb_chicks = []          # number of chicks alive
    plot_tank_value = []    # plots that show the food tank evolution
    plot_tank_key = []      # keys for the plots "plot_tank_value"
    tank_cycle_value = []   # value of the tank cycle
    tank_cycle_key = []     # keys for the plots "tank_cycle_value"

    # read file with the result of "test_of()"
    with open("lab02/src/log.txt") as fp:
        # read each line
        for line in fp:
            elt = line.strip().split(" ")
            print(line.strip())
            try:
                # try to read the time
                x = int(elt[0])  # if not a int value, this isn't an event
                y = elt[2] + " " + elt[3]

                # add a dot at the correct position and choose the color with the action
                ax1.scatter(x, y, c=GRAPH_COLORS[elt[5]])

                # do special action with some event
                if elt[5] == "eating":  # update the nest evolution graph
                    # fill last lines
                    for i in range(last_eat, x-1):
                        tank_cycle_value.append(nest_food)
                        tank_cycle_key.append(i)

                    # update current values
                    nest_food -= 1
                    ax2.scatter(x, nest_food, c="red")
                    tank_cycle_value.append(nest_food)
                    tank_cycle_key.append(x)
                    last_eat = x

                    # add current tank evolution graph to the graphlist
                    if nest_food == 0:
                        plot_tank_value.append(tank_cycle_value)
                        plot_tank_key.append(tank_cycle_key)
                        tank_cycle_value = []
                        tank_cycle_key = []

                elif elt[5] == "throwing":
                    # start a new tank evolution graph
                    if g_nb_threads != 0:
                        nest_food += int(elt[7])
                        ax2.scatter(x, nest_food, c="forestgreen")
                        tank_cycle_value.append(nest_food)
                        tank_cycle_value.append(nest_food)
                        tank_cycle_key.append(x)
                        tank_cycle_key.append(x)
                        last_eat = x

                # update the number of chicks graph
                if elt[5] == "ready":
                    g_nb_threads -= 1
                for i in range(last_x, x):
                    nb_chicks.append(g_nb_threads)

                # add the last tank evolution graph
                if g_nb_threads == 0:
                    plot_tank_value.append(tank_cycle_value)
                    plot_tank_key.append(tank_cycle_key)
                    tank_cycle_value = []
                    tank_cycle_key = []

                # update value
                last_x = x

            # Manage case when a line isn't an event
            except ValueError:
                print("Not a log")

    # fill the nest evolution graph
    for i in range(len(plot_tank_value)):
        ax2.plot(plot_tank_key[i], plot_tank_value[i])
    # fill the number of chicks graph
    ax3.plot(range(len(nb_chicks)), nb_chicks, color="black")

    # add legend
    for key, val in GRAPH_COLORS.items():
        ax3.scatter(-1, -0, c=val, label=key)
    ax3.scatter(-1, 0, c="white", s=100)
    lines, labels = ax3.get_legend_handles_labels()
    fig.legend(lines, labels, loc='upper right')

    # display the plot
    plt.show()


# run test that cannot be run with pytest
if __name__ == '__main__':
    # run_infinity()
    # test_ok()
    show_graph()
