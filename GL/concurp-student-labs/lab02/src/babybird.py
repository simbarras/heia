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
Simulate a bird's nest with threads and semaphores
"""

__author__ = "Simon Barras"
__date__ = "2021-11-12"
__version__ = "0.1.0"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"

import threading    # Thread and semaphore
import random       # Random number generator
import logging      # Print log in console with information of the thread
import sys          # Usefull for paramters
import names        # library for adding random names

# Constants
WAIT_BIRD = 10      # Maximum wait time for a bird
WAIT_MAX = 50       # Maximum wait time for a an method
NB_OF_PARENTS = 2
MAX_ARGS = 4
SUCCESS_RATE_INDEX = 3
# Default values
DEFAULT_NB_CHICKS = 17
DEFAULT_BABY_ITR = 53
DEFAULT_FOOD_CAPACITY = 7
DEFAULT_HUNTING_RATE = 50

# Global variables
g_nb_day_to_be_a_chicken: int
g_chicks = []
g_parents = []
g_nb_of_worm = 0
g_food_capacity: int
g_hunting_success_rate: int

# Semaphores
sem_wait_eat = threading.Semaphore(0)
lock_worm_tank = threading.Semaphore(1)
lock_tank_fill = threading.Semaphore(1)


class Chick():
    """
    Object for simulating a baby bird.
    Use a thread and routine to live.
    """
    name: str
    spirit: threading.Thread
    speed: float
    live: threading.Semaphore

    def __init__(self):
        self.spirit = threading.Thread(target=self.live)
        self.speed = random.randrange(WAIT_BIRD)

    # Start the thread and the routine
    def born(self, name: str = ""):
        self.name = "Chick " + name
        self.live = threading.Semaphore(0)
        self.spirit.start()

    # Routine of the bird's life
    def live(self):
        for _ in range(g_nb_day_to_be_a_chicken):
            self.sleep()
            self.get_food()
            self.eat()
            self.digest_and___()

        self.leave_the_nest()

    # Wait a random time
    def sleep(self):
        logging.debug("%s is sleeping", self.name)
        nap(self.speed)

    # Take food only if the tank isn't empty and nobody is using the variable "g_nb_of_worm"
    def get_food(self):
        logging.debug("%s is filling his plate", self.name)
        global g_nb_of_worm
        sem_wait_eat.acquire()
        with lock_worm_tank:
            g_nb_of_worm -= 1
            if g_nb_of_worm == 0:
                lock_tank_fill.release()

    # Random timer
    def eat(self):
        logging.debug("%s is eating", self.name)
        nap(self.speed)

    # Random timer
    def digest_and___(self):
        logging.debug("%s is digesting", self.name)
        nap(self.speed)

    # last method of the life cycle
    # Release the semaphore
    def leave_the_nest(self):
        logging.debug("%s is ready to take the plunge", self.name)
        self.live.release()

    # Call by main thread for waiting until the bird left the nest
    def farewell_party(self):
        self.live.acquire()

    # Close the thread
    def funeral(self):
        self.spirit.join()
        logging.debug("%s is buried", self.name)


class Parent():
    """
    Object for simulating a parent bird.
    Use a thread and routine to live.
    """
    name: str
    spirit: threading.Thread
    speed: float
    has_child: bool
    hunting_result: int

    def __init__(self):
        self.spirit = threading.Thread(target=self.live)
        self.speed = random.randrange(WAIT_BIRD)
        self.has_child = False

    # Start the thread and the routine
    def born(self, name: str = ""):
        self.name = "Parent " + name
        self.hunting_result = 0
        self.has_child = True
        self.spirit.start()

    # Routine of the bird's life
    def live(self):
        while(self.has_child):
            self.hunt()
            self.deposit_food()
            self.rest()

    # Wait a random time and then try to hunt.
    # The probability for the parent to find something is define ine the parameters.
    # The number of food find is a random number: 0 < food <= food capacity.
    def hunt(self):
        logging.debug("%s is hunting", self.name)
        nap(self.speed)
        if random.randrange(1, 101) <= g_hunting_success_rate:
            self.hunting_result = random.randrange(1, g_food_capacity+1)

    # Put food only if the tank is empty and nobody is using the variable "g_nb_of_worm"
    def deposit_food(self):
        global g_nb_of_worm
        if self.hunting_result > 0:
            lock_tank_fill.acquire()
            with lock_worm_tank:
                g_nb_of_worm = self.hunting_result      # With python 3.9.5
                for _ in range(self.hunting_result):    # sem_wait_eat.release(self.hunting_result) == sem_wait_eat.release(1)
                    sem_wait_eat.release()              # to fix this, I have use a loop
            logging.debug("%s is throwing up %d worm",
                          self.name, self.hunting_result)
            self.hunting_result = 0

    # Random timer
    def rest(self):
        nap(self.speed)
        logging.debug("%s is taking a coffee", self.name)

    # Unlock the routine and release the tank nest for unlock parent
    def mothers_sorrow(self):
        self.has_child = False
        lock_tank_fill.release()
        logging.debug("%s is dying of sorrow", self.name)

    # Close the thread
    def funeral(self):
        self.spirit.join()
        logging.debug("%s is buried", self.name)


# Random timer
def nap(bird_speed: int):
    threading.Event().wait((bird_speed + random.randrange(WAIT_MAX - WAIT_BIRD))/1000)


# Initialize values
def building_nest(chicks=DEFAULT_NB_CHICKS,
                  baby_itr=DEFAULT_BABY_ITR,
                  food_capacity=DEFAULT_FOOD_CAPACITY,
                  hunting_success_rate=DEFAULT_HUNTING_RATE):
    global g_nb_day_to_be_a_chicken, g_chicks, g_parents, g_food_capacity, g_hunting_success_rate
    g_nb_day_to_be_a_chicken = baby_itr
    g_food_capacity = food_capacity
    g_hunting_success_rate = hunting_success_rate
    for _ in range(chicks):
        g_chicks.append(Chick())
    for _ in range(NB_OF_PARENTS):
        g_parents.append(Parent())


# Run the nest life
def simulating():
    for chick in g_chicks:
        chick.born(name=names.get_first_name())
    for parent in g_parents:
        parent.born(names.get_first_name())
    for chick in g_chicks:
        chick.farewell_party()
    for parent in g_parents:
        parent.mothers_sorrow()


# Close all threads
def destroy_nest():
    for chick in g_chicks:
        chick.funeral()
    for parent in g_parents:
        parent.funeral()


# Main function
def main(args):
    print("Lab02 - Babybird simulation")
    building_nest(*args)
    simulating()
    destroy_nest()
    print("Lab02 - finish simulation")
    pass


# Format args
def parse_arg(argv):
    try:
        args_result = []
        for i in range(len(argv)):
            if check_args(i, argv[i]):
                args_result.append(int(argv[i]))
            else:
                raise ValueError

        return args_result
    except ValueError:
        print("Error: Invalid argument, please follow usage below")
        print(
            "python3 babybird.py [chicks [baby_iter [max_food_size [hunting_success_rate]]]]")
        sys.exit(False)


# check type and value of arguments. Check the number of args too
def check_args(i, arg):
    if i >= MAX_ARGS:
        return False
    if i == SUCCESS_RATE_INDEX:
        return 0 <= int(arg) <= 100
    else:
        return int(arg) > 0


# main program entry point
if __name__ == "__main__":
    logging.basicConfig(
        stream=sys.stdout,
        format='%(relativeCreated)6d %(threadName)s %(message)s',
        level=logging.DEBUG
    )
    args = parse_arg(sys.argv[1:])  # remove the first element of argv
    main(args)
    sys.exit(True)
