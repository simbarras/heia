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
Download a range of pokemon's picture.
The number of thread or process is configurable
"""

__author__ = "Simon Barras"
__date__ = "2021-10-15"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"

import time

from getpokemon import download_image
import sys
import threading
import multiprocessing


k_max_pokemon = 898
k_default_verbose = False
k_nb_args = 5
args_ = {
    "min_pokemon": 1,
    "max_pokemon": k_max_pokemon,
    "tp": "T",
    "nbr_tp": 6,
    "path": "./tmp"
}
tp_list_ = []
tp_result_ = []
count_ = 0
do_stats_ = False


# This method can have some concurrent problems
def worker_canHaveConcurrentProblem(index):
    global count_
    while count_ < args_['max_pokemon']:
        # Protection de la section critique
        count_ += 1
        #print("TD: " + str(index) + " is downloading " +str(count))
        try:
            download_image(count_, args_["path"], k_default_verbose)
        except Exception as e:
            continue
        if do_stats_:
            global tp_result_
            tp_result_[index] += 1


def worker(index):
    # Download all image from the beginning to the end
    # Increment value with the number of TP
    for i in range(1 + index, args_['max_pokemon'], args_['nbr_tp']):
        #print("TD: " + str(index) + " is downloading " +str(i))
        try:
            download_image(i, args_["path"], k_default_verbose)
        except Exception as e:
            continue
        if do_stats_:
            global tp_result_
            tp_result_[index] += 1


def initialize():
    global count_
    count_ = args_['min_pokemon'] - 1
    for i in range(0, args_["nbr_tp"]):
        if args_["tp"] == "T":
            tp = threading.Thread(target=worker, args=(i, ))
        else:
            tp = multiprocessing.Process(target=worker, args=(i, ))

        tp_list_.append(tp)


def clear():
    global tp_result_
    tp_list_.clear()
    tp_result_.clear()
    tp_result_ = [0] * args_["nbr_tp"]


def main(args=args_, give_stat=False):
    """ main function """
    global args_
    args_ = args
    global do_stats_
    do_stats_ = give_stat

    # Clear all residual values
    clear()

    # Initialize return value if is not necessary
    if not do_stats_:
        tp_result_.append(0)

    # Initialize all values
    #   Create thread/process
    initialize()

    # Start threads/process
    for tp in tp_list_:
        tp.start()

    # Wait until threads/process finish
    for tp in tp_list_:
        tp.join()

    return tp_result_


# main program entry point
if __name__ == "__main__":
    for i in range(1, len(sys.argv)):
        if i == 1:
            args_["min_pokemon"] = int(sys.argv[i])
        elif i == 2:
            args_["max_pokemon"] = int(sys.argv[i])
        elif i == 3:
            args_["tp"] = sys.argv[i]
        elif i == 4:
            args_["nbr_tp"] = int(sys.argv[i])
        elif i == 5:
            args_["path"] = sys.argv[i]
    start = time.time()
    main()
    end = time.time()
    print("Time %f" % (end - start))
