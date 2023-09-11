// Copyright 2021 Haute école d'ingénierie et d'architecture de Fribourg
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/****************************************************************************
 * @file color.c
 * @author Jacques Supcik <jacques.supcik@hefr.ch>
 *
 * @brief Simple color library
 *
 * @date 2021-12-19
 * @version 0.1.0
 ***************************************************************************/

#include "color.h"
#include <math.h>

static unsigned int hslColor(double h, double s, double l)
{
    double c = 1 - fabs(2 * l - 1) * s;  // chroma
    double x = c * (1 - fabs(fmod(h / 60.0, 2.0) - 1));
    double m = l - c / 2;
    double r, g, b;
    if (h < 60) {
        r = c;
        g = x;
        b = 0;
    } else if (h < 120) {
        r = x;
        g = c;
        b = 0;
    } else if (h < 180) {
        r = 0;
        g = c;
        b = x;
    } else if (h < 240) {
        r = 0;
        g = x;
        b = c;
    } else {
        r = c;
        g = 0;
        b = x;
    }

    int ri = (r + m) * 0x1f;
    int gi = (g + m) * 0x3f;
    int bi = (b + m) * 0x1f;

    return ((ri & 0x1f) << 11) | ((gi & 0x3f)) << 5 | (bi & 0x1f);
}

void MakeRainbow(unsigned int* array, int size)
{
    for (int i = 0; i < size; i++) {
        array[i] = hslColor(i * 360.0 / size, 1.0, 0.5);
    }
}
