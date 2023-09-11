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
Simulate the interaction between clients and a printers.
A central server manage who can print and who must wait.
"""

__author__ = "Simon Barras"
__date__ = "2021-12-21"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


# IMPORTS
import logging      # Print log thread-safe
import argparse     # parser for paramaters
import argChecker   # Check args
import sys  # TODO
from printers import Printer # TODO
from multiprocessing import Queue # TODO
from multiprocessing import Manager # TODO
from printServer import PrintServer
from client import Client



# CONSTANTS
DEFAULT_NB_A = 19       # Default number of clients who print on A
DEFAULT_NB_B = 17       # Default number of clients who print on B
DEFAULT_NB_AB = 19      # Default number of clients who print on A and B
DEFAULT_NB_FILES = 11   # Default number of files to print
DEFAULT_NB_LINES = 13   # Default number of lines to print


# Main method. Life cycle of the app
def main(nb_a, nb_b, nb_ab, nb_files, nb_lines):
    print("Lab05 - The printer server")
    manager = Manager()
    # Create the server
    server_channel = manager.Queue()
    server = PrintServer(server_channel)
    
    # Create a printer
    printerA = Printer(True, manager.Queue())
    printerB = Printer(False, manager.Queue())
    
    # Create the client
    clients = []
    id = 0
    for i in range(nb_a):
        client = Client('a', id, nb_lines, nb_files, server_channel, manager.Queue())
        clients.append(client)
        id += 1
    for i in range(nb_b):
        client = Client('b', id, nb_lines, nb_files, server_channel, manager.Queue())
        clients.append(client)
        id += 1
    for i in range(nb_ab):
        client = Client('ab', id, nb_lines, nb_files, server_channel, manager.Queue())
        clients.append(client)
        id += 1
    
    # Start all
    server.start()
    printerA.on(server_channel)
    printerB.on(server_channel)
    for client in clients:
        client.on()
    
    # Stop all
    for client in clients:
        client.off()
    server.off()
    printerA.off()
    printerB.off()
    


# main program entry point
if __name__ == "__main__":
    logging.basicConfig(                                            # Configure the logging
        stream=sys.stdout,                                          # Output to console
        format='%(relativeCreated)6d %(threadName)s %(message)s',   # Format of the log
        level=logging.DEBUG                                          # Level of the log
    )

    # Create the parser
    parser = argparse.ArgumentParser(description=__doc__,
                                     epilog=f"""Copyright 2021, School of Engineering and Architecture of Fribourg,
                                        Author: {__author__} <{__email__}>""")
    # Add the arguments
    parser.add_argument("--nbA",
                        metavar=">= 0",
                        type=int,
                        default=DEFAULT_NB_A, help="number of clients that must use printer A",
                        action=argChecker.CLIENT_NUMBER)

    parser.add_argument("--nbB",
                        metavar=">= 0",
                        type=int,
                        default=DEFAULT_NB_B, help="number of clients that must use printer B",
                        action=argChecker.CLIENT_NUMBER)

    parser.add_argument("--nbAB",
                        metavar=">= 0",
                        type=int,
                        default=DEFAULT_NB_AB, help="number of clients that must use printer A and B",
                        action=argChecker.CLIENT_NUMBER)

    parser.add_argument("--nb_files",
                        metavar=">= 1",
                        type=int,
                        default=DEFAULT_NB_FILES, help="""Maximum number of files that a client will print.
                            A client randomly picks a number between 1 and nb_files (inclusive) which will be
                            the number of files that a client will print""",
                        action=argChecker.FILE_NUMBER)

    parser.add_argument("--nb_lines",
                        metavar=">= 1",
                        type=int,
                        default=DEFAULT_NB_LINES, help="""Maximum number of 'lines' a file has. For each file,
                            a client selects a number randomly between 1 and nb_lines (inclusive) to send to
                            the printer for printing""",
                        action=argChecker.FILE_NUMBER)

    # Parse the arguments
    args = parser.parse_args()

    main(args.nbA, args.nbB, args.nbAB, args.nb_files, args.nb_lines)
