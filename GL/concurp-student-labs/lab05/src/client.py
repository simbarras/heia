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
Simulate a client and print.
"""

__author__ = "Simon Barras"
__date__ = "2021-12-21"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


# IMPORTS
import imp
import logging
from multiprocessing import Process
from multiprocessing import Queue
import time
import random
import printServer
from message import Message


# CONSTANTS


# Client


class Client:
    """
    TODO
    """

    process: Process
    type: int
    id: int
    max_lines: int
    files_to_print: int
    server: Queue
    response_queue: Queue
    
    def __init__(self, type, id, max_lines, max_files, server, queue):
        # type: 0 = A, 1 = AB, 2 = B
        self.type = type
        self.max_lines = max_lines
        self.id = id
        self.server = server
        self.response_queue = queue
        self.files_to_print = random.randint(1, max_files+1)
        self.process = Process(target=self.run)
        
    def on(self):
        self.process.start()
    
    def run(self):
        logging.info("Client %d is running", self.id)
        self.server.put(['client', 1])
        for i in range(self.files_to_print):
            
            # Ask for a print
            self.server.put(['print', self.type, self.response_queue])
            # Get the print
            printer: Queue = self.response_queue.get()
            # print
            nb_lines = random.randint(1, self.max_lines+1)
            for j in range(1, nb_lines+1):
                msg = Message(self.type, self.id, i, j)
                printer.put(msg)
            
            # Return the printer
            self.server.put(['register', self.type, printer])
            
        # Ask for a stop
        self.server.put(['client', -1])
        
    def off(self):
        self.process.join()
