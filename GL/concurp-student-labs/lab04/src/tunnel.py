#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Copyright 2021, School of Engineering and Architecture of Fribourg
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
Simulation of a one-lane tunnel.
"""

__author__ = "Simon Barras"
__date__ = "2021-12-07"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


#### Import ####
from TunnelMonitor import TunnelMonitor  # my monitor for the threads
from TunnelMonitor import Vehicle        # Class that simulate the cars
import logging                           # print log in console with information of the thread
import argparse                          # parser for paramaters
import sys                               # Work with the computer


#### DEFAULT ####
CAR_NORTH = 29    # number of cars going north
CAR_SOUTH = 31    # number of cars going south
CYCLE_NORTH = 23  # number of time a car is going north
CYCLE_SOUTH = 19  # number of time a car is going south
LIMIT = 5         # Max number of cars going in the same direction


#### Methods ####

# Main method. Life cycle of the app
def main(car_north, car_south, cycle_north, cycle_south, limit):
    print("Lab04 - The one-lane tunnel simulation")
    print("{} cars going north, {} cars going south, {} cycles north, {} cycles south, limit {}".format(
        car_north, car_south, cycle_north, cycle_south, limit))
    tunnel_monitor = TunnelMonitor(limit)
    print(tunnel_monitor.__doc__)

    cars = []

    for i in range(car_north):
        cars.append(Vehicle(cycle_north, True, tunnel_monitor, "North: {}".format(i)))
    for i in range(car_south):
        cars.append(Vehicle(cycle_south, False, tunnel_monitor, "South: {}".format(i)))

    for car in cars:
        car.jump_in_car()

    for car in cars:
        car.get_out_of_car()


# Format args
class CAR_ACTION(argparse.Action):
    """
    Check if the number of cars is a positive integer
    """

    def __call__(self, parser, namespace, values, option_string=None):
        if values < 0:
            parser.error("The number of cars must be a positive integer")
        setattr(namespace, self.dest, values)


class CYCLE_ACTION(argparse.Action):
    """
    Check if the number of cycles is a positive integer
    """

    def __call__(self, parser, namespace, values, option_string=None):
        if values <= 0:
            parser.error("The number of cycles must be a positive integer")
        setattr(namespace, self.dest, values)


class LIMIT_ACTION(argparse.Action):
    """
    Check if the number of cars is a positive integer
    """

    def __call__(self, parser, namespace, values, option_string=None):
        if values <= 0:
            parser.error("The number of cars must be a positive integer")
        setattr(namespace, self.dest, values)


# main program entry point
if __name__ == "__main__":
    logging.basicConfig(                                           # Configure the logging
        stream=sys.stdout,                                         # Output to console
        format='%(relativeCreated)6d %(threadName)s %(message)s',  # Format of the log
        level=logging.INFO                                        # Level of the log
    )

    # Create the parser
    parser = argparse.ArgumentParser(description=__doc__,
                                     epilog="""Copyright 2021, School of Engineering and Architecture of Fribourg, 
                                     Author: {} <{}>""".format(__author__, __email__))
    # Add the arguments
    parser.add_argument("-N", metavar=">= 0", type=int,
                        default=CAR_NORTH, help="Number of cars going north", action=CAR_ACTION)
    parser.add_argument("-S", metavar=">= 0", type=int,
                        default=CAR_SOUTH, help="Number of cars going south", action=CAR_ACTION)
    parser.add_argument("-n", metavar=">= 1", type=int,
                        default=CYCLE_NORTH, help="Number of time a car going north", action=CYCLE_ACTION)
    parser.add_argument("-s", metavar=">= 1", type=int,
                        default=CYCLE_SOUTH, help="Number of time a car going south", action=CYCLE_ACTION)
    parser.add_argument("-L", metavar=">= 1", type=int, default=LIMIT,
                        help="Max number of cars going in the same direction", action=LIMIT_ACTION)

    # Parse the arguments
    args = parser.parse_args()
    main(args.N, args.S, args.n, args.s, args.L)
