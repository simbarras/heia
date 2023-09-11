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
Check args.
"""

__author__ = "Simon Barras"
__date__ = "2021-12-21"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


# IMPORTS
import argparse    # parser for paramaters


# CHECKERS
class CLIENT_NUMBER(argparse.Action):
    """
    Check if the number of clients is a positive integer or 0.
    """

    def __call__(self, parser, namespace, value, option_string=None) -> None:
        if value < 0:
            parser.error("The number of clients must be a positive integer.")
        setattr(namespace, self.dest, value)


class FILE_NUMBER(argparse.Action):
    """
    Check if the number of files or lines is a positive integer.
    """

    def __call__(self, parser, namespace, value, option_string=None) -> None:
        if value < 1:
            parser.error("The number of files or lines must be a positive integer.")
        setattr(namespace, self.dest, value)