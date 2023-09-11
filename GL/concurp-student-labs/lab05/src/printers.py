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
Simulate a printers.
"""

__author__ = "Simon Barras"
__date__ = "2021-12-21"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


# IMPORTS
from cgi import test
from multiprocessing import Queue, Process    # TODO
import logging      # Print log thread-safe
import time         # Sleep for a while
from datetime import datetime     # Get the current time
from message import Message      # Message class

# CONSTANTS
DIRECTORY = "./lab05/printer_out"
A_FILE = f"{DIRECTORY}/printerA.txt"
B_FILE = f"{DIRECTORY}/printerB.txt"


# PRINTER
class Printer:
    """
    TODO
    """

    name: str   # TODO
    type: bool  # TODO
    running = True
    process:  Process  # TODO
    queue: Queue
    file: str  # File to print

    def __init__(self, type: bool, queue):
        """
        Turn on the printer.
        """
        self.type = type

        if type:
            self.name = 'a'
            self.file = A_FILE
        else:
            self.name = 'b'
            self.file = B_FILE
        self.queue = queue
        self.process = Process(target=self.routine)


    def on(self, server: Queue):
        text = f"Printer {self.name} is on."
        logging.info(text)
        with open(self.file, 'w') as f:
            f.write(f"{text}\n")

        server.put(["register", self.name, self.queue]) # Register the printer at the server
        self.queue.put(Message(self.name, -1, -1, -1))
        self.process.start()

    def routine(self):
        while self.running:
            msg: Message = self.queue.get()
            if str(msg) == "stop":
                self.running = False
                break
            self.print(msg)
            #self.queue.put(Message(self.name, i, i, i))

    def print(self, msg: Message, mode='a'):
        # open file and add message
        timestamp = datetime.now().strftime("%H:%M:%S:%f")
        client_type, client_no, file_no, line_no = msg.info()
        text = f"Client_{client_type} #{client_no}, {timestamp}, file {file_no}, line {line_no}"
        logging.info(f"Printer {self.name} prints {text}")

        # append 'text' to file
        with open(self.file, mode) as f:
            f.write(f"{text}\n")

    def off(self):
        self.process.join()
        text = f"Printer {self.name} is off."
        logging.info(text)
        with open(self.file, 'a') as f:
            f.write(f"{text}\n")

# METHODS


def nap(name):
    """
    Sleep for a while.
    """
    logging.info(f"{name} Nap.")
    time.sleep(0.001)  # TODO Magic number
