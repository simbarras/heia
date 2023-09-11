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

"""Monitor and cars for the one-lane monitor"""

__author__ = "Simon Barras"
__date__ = "2021-12-07"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"

#### Import ####
from threading import Condition  # Condition for the monitor
from threading import Lock       # Lock for the monitor
from threading import Thread     # threads
from threading import Event      # Event for wait
import random as rnd             # lib for random number
import logging                   # print the log thread safe


#### CONSTANTS ####
MAX_SPEED_CAR = 10              # Max time car character
MAX_SPEED = 50 - MAX_SPEED_CAR  # Max time
MILLISECONDS = 1000             # Number of milliseconds in a second


# initialization of the class and the monitor with default values
class TunnelMonitor:
    """The tunnel monitor"""

    monitor_lock: Lock          # lock for the monitor
    north_condition: Condition  # Catch the north car
    south_condition: Condition  # Catch the south car
    limit: int                  # limit of car in a row before a change
    limit_north: int            # limit of car of north
    limit_south: int            # limit of car of south
    counter_north: int          # count the number of north car in the tunnel
    counter_south: int          # count the number of south car in the tunnel
    nb_cars_north: int          # count the number of north waitting car
    nb_cars_south: int          # count the number of south waitting car

    # initialize the tunnel monitor
    def __init__(self, limit):
        self.monitor_lock = Lock()
        self.wait_north = Condition(self.monitor_lock)  # some magical CV
        self.wait_south = Condition(self.monitor_lock)  # some magical CV

        self.limit = limit
        self.limit_north = limit
        self.limit_south = limit
        self.counter_north = 0
        self.counter_south = 0
        self.nb_cars_north = 0
        self.nb_cars_south = 0

    # call by car entering on the north side
    def enter_north(self):
        with self.monitor_lock:
            logging.debug(
                f"""Enter north\n
                \t north {self.nb_cars_north}, counter {self.counter_north}, limit {self.limit_north}\n
                \t south {self.nb_cars_south}, counter {self.counter_south}, limit {self.limit_south}""")
            self.nb_cars_north += 1
            while self.counter_south > 0 or self.limit_north <= 0:
                self.wait_north.wait()
                logging.debug(
                    f"""wake up north\n
                    \t north {self.nb_cars_north}, counter {self.counter_north}, limit {self.limit_north}\n
                    \t south {self.nb_cars_south}, counter {self.counter_south}, limit {self.limit_south}""")

            self.limit_south = self.limit
            self.counter_north += 1
            self.nb_cars_north -= 1
            if self.nb_cars_south > 0:
                self.limit_north -= 1
            else:
                self.wait_north.notifyAll()

    # call by car entering on the south side
    def enter_south(self):
        with self.monitor_lock:
            logging.debug(
                f"""Enter south\n
                \t north {self.nb_cars_north}, counter {self.counter_north}, limit {self.limit_north}\n
                \t south {self.nb_cars_south}, counter {self.counter_south}, limit {self.limit_south}""")
            self.nb_cars_south += 1
            while self.counter_north > 0 or self.limit_south <= 0:
                self.wait_south.wait()
                logging.debug(
                    f"""wake up south\n
                    \t north {self.nb_cars_north}, counter {self.counter_north}, limit {self.limit_north}\n
                    \t south {self.nb_cars_south}, counter {self.counter_south}, limit {self.limit_south}""")

            self.limit_north = self.limit
            self.counter_south += 1
            self.nb_cars_south -= 1
            if self.nb_cars_north > 0:
                self.limit_south -= 1
            else:
                self.wait_south.notifyAll()

    # call by car exiting on the south side
    def exit_north(self):
        with self.monitor_lock:
            logging.debug(
                f"""Exit north\n
                \t north {self.nb_cars_north}, counter {self.counter_north}, limit {self.limit_north}\n
                \t south {self.nb_cars_south}, counter {self.counter_south}, limit {self.limit_south}""")
            self.counter_north -= 1
            for _ in range(self.limit):
                self.wait_south.notify()

    # call by car exiting on the north side
    def exit_south(self):
        with self.monitor_lock:
            logging.debug(
                f"""Exit south\n
                \t north {self.nb_cars_north}, counter {self.counter_north}, limit {self.limit_north}\n
                \t south {self.nb_cars_south}, counter {self.counter_south}, limit {self.limit_south}""")
            self.counter_south -= 1
            for _ in range(self.limit):
                self.wait_north.notify()


class Vehicle:
    """
    Simulate a car
    """

    motor: Thread           # Thread of the cars
    north: bool             # is the car going to the north
    tunnel: TunnelMonitor   # the tunnel
    name: str               # name of the car
    speed: int              # speed of the car

    def __init__(self, cycle: int, north: bool, tunnel: TunnelMonitor, name: str = ""):
        self.north = north
        self.motor = Thread(target=self.drive, args=(cycle,))
        self.tunnel = tunnel
        self.name = name
        self.speed = rnd.randrange(MAX_SPEED_CAR)

    # Start the thread
    def jump_in_car(self):
        logging.info("{} is jumping in the car".format(self.name))
        self.motor.start()

    # Routine of the thread
    def drive(self, cycle: int):
        nap(self.speed)
        for _ in range(cycle):
            self.drive_to_tunnel_entrance()
            self.wait_for_entrance_permission()
            self.drive_though_tunnel()
            self.exit_tunnel()
            self.drive_to_origin()

    # Random timer
    def drive_to_tunnel_entrance(self):
        logging.info("{} is going to the tunnel entrance".format(self.name))
        nap(self.speed)

    # Wait until the tunnel is open
    def wait_for_entrance_permission(self):
        logging.info("{} is waiting for the entrance permission".format(self.name))
        if self.north:
            self.tunnel.enter_north()
        else:
            self.tunnel.enter_south()

    # Random timer
    def drive_though_tunnel(self):
        logging.info("{} is driving through the tunnel".format(self.name))
        nap(self.speed)

    # Leave the tunnel
    def exit_tunnel(self):
        logging.info("{} is leaving the tunnel".format(self.name))
        if self.north:
            self.tunnel.exit_north()
        else:
            self.tunnel.exit_south()

    # Go to origin postion, random timer
    def drive_to_origin(self):
        logging.info("{} is going back to the origin".format(self.name))
        nap(self.speed)

    # kill the thread
    def get_out_of_car(self):
        self.motor.join()
        logging.info("{} is getting out of the car".format(self.name))


# Random timer
def nap(time: int):
    wait_time = time + rnd.randrange(MAX_SPEED)
    Event().wait(wait_time / MILLISECONDS)
