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
Simulate a printer server.
"""

__author__ = "Simon Barras"
__date__ = "2021-12-21"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


# IMPORTS
from multiprocessing import Process
from multiprocessing import Queue

# CONSTANTS


# SERVER
class PrintServer:
    """
    TODO
    """

    running: bool
    nb_client: int
    printers: list
    clients: list
    server_channel: Queue
    process: Process

    def __init__(self, channel):
        self.running = True
        self.nb_client = 0
        self.printers = []
        self.clients = []
        self.server_channel = channel
        self.process = Process(target=self.routine)

    def start(self):
        self.process.start()

    def routine(self):
        while self.running:
            # Get the next request
            request = self.server_channel.get()
            # [Method, args, Queue]
            method = request[0]

            # Send a printer's queue to the client
            if method == "print":
                self.search_printer(request[1], request[2])

            # Add a new printer
            elif method == "register":
                self.search_client(request[1], request[2])

            # Add or remove a client
            elif method == "client":
                arg = int(request[1])
                self.nb_client += arg
            else:
                print("Unknown method")  # TODO

            if self.nb_client == 0:
                self.running = False
                break

        for printer in self.printers:
            printer[1].put("stop")

    def search_printer(self, type, queue):
        for printer in self.printers:
            if printer[0] == type:
                self.printers.remove(printer)
                printer[1].put(queue)
                return
        print("No printer available")  # TODO
        self.clients.append([type, queue])

    def search_client(self, type, queue):
        for client in self.clients:
            if client[0] == type:
                self.clients.remove(client)
                client[1].put(queue)
                return
        print("No client available")
        self.printers.append([type, queue])

    def off(self):
        self.process.join()
