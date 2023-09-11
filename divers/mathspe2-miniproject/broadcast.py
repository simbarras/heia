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
RSA Algorithm
"""

__author__ = "Barras Simon & Terreaux Nicolas"
__date__ = "2022-05-02"
__version__ = "0.0.1"
__email__ = "simon.barras@edu.hefr.ch & nicolas.terreaux@edu.hefr.ch"
__userid__ = "simon.barras & nicolas.terreaux"

import numpy as np
import rsa

NA_DEFAULT = 377
CA_DEFAULT = 330
NB_DEFAULT = 391
CB_DEFAULT = 34
NC_DEFAULT = 589
CC_DEFAULT = 419
M_DEFAULT = 102


def tx(c1, n1, n2, n3):
    inv = rsa.inverse(n2 * n3, n1)
    return c1 * n2 * n3 * inv


def find_c(ta, tb, tc, na, nb, nc):
    return (ta + tb + tc) % (na * nb * nc)


def hastad(na, ca, nb, cb, nc, cc):
    print(f"Start program with (A:{ca} mod {na}, B:{cb} mod {nb}, C:{cc} mod {nc})")
    print("Find tx")
    ta = tx(ca, na, nb, nc)
    tb = tx(cb, nb, nc, na)
    tc = tx(cc, nc, na, nb)
    print(f"ta:\t{ta}\ntb:\t{tb}\ntc:\t{tc}")
    print("Find c")
    c = find_c(ta, tb, tc, na, nb, nc)
    m = np.cbrt(c)
    print(f"c:\t{c}\nm:\t{m}")


if __name__ == '__main__':
    M = 223972
    E = 3
    N1 = 77201053
    N2 = 72885013
    N3 = 377
    p1, q1, phi1, d1, c1_, s1, m1 = rsa.main(N1, E, M)
    p2, q2, phi2, d2, c2, s2, m2 = rsa.main(N2, E, M)
    p3, q3, phi3, d3, c3, s3, m3 = rsa.main(N3, E, M)
    hastad(N1, c1_, N2, c2, N3, c3)
    hastad(NA_DEFAULT, CA_DEFAULT, NB_DEFAULT, CB_DEFAULT, NC_DEFAULT, CC_DEFAULT)
