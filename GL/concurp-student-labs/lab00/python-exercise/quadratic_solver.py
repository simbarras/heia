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
A very easy program who solve quadratic equation
"""

import math

__author__ = 'Simon Barras'
__date__ = "2020-09-21"
__version__ = "0.2"
__email__ = "simon.barras@edu.hefr.ch"
__userid__ = "simon.barras"


class Solver:
    # solve quadratic equation
    def quadratic(self, a, b, c):
        delta = b**2 - 4 * a * c
        if delta > 0:
            sqrt_delta = math.sqrt(delta)
            root1 = (-b + sqrt_delta) / (2 * a)
            root2 = (-b - sqrt_delta) / (2 * a)
            return root1, root2
        elif delta == 0:
            return -b / (2 * a)
        else:
            return "This equation has no roots"


def main():
    solver = Solver()

    while True:
        # ask the 3 composant of the equation
        a = float(input("a: "))
        b = float(input("b: "))
        c = float(input("c: "))
        result = solver.quadratic(a, b, c)
        print(result)


# main program entry point
if __name__ == "__main__":
    main()
