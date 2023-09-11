// Copyright 2022 Haute école d'ingénierie et d'architecture de Fribourg
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
 * @file seven_segments.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Seven segment display that can display number.
 *
 * @date 2022.02.13 to 2020.03.3
 * @version 0.0.1
 ***************************************************************************/
#include "seven_segments.hpp"

#include <math.h>

#include "mbed.h"
#include "mbed_trace.h"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "SevenSegments"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

constexpr int kDash       = 0b10000000;
constexpr int kDashDotted = 0b10000001;
const uint8_t kPatterns[] = {
    0b0111111,  // 0
    0b0000101,  // 1
    0b1011011,  // 2
    0b1001111,  // 3
    0b1100101,  // 4
    0b1101110,  // 5
    0b1111110,  // 6
    0b0000111,  // 7
    0b1111111,  // 8
    0b1101111,  // 9
    0b1110111,  // A
    0b1111000,  // b
    0b0111010,  // C
    0b1011101,  // d
    0b1111010,  // E
    0b1110010,  // F
};

// 0b mid top-left bottom-left bottom bottom-right top top-right

SevenSegments::SevenSegments(int place)
    : display(PA_7, PA_6, PA_5),
      brightness(place == 0 ? PF_10 : PF_3),
      reset(place == 0 ? PC_3 : PC_4),
      latch(place == 0 ? PB_8 : PA_15)
{
    tr_info("Seven Segments at place %d", place);
    switch (place) {
        case 1:
            break;
        case 0:
            tr_warning("Not implemented yet");
            break;
        default:
            tr_error("Place not available");
            break;
    }

    this->display.format(8, 0);
    this->display.frequency(1000000);
    this->brightness = 1;
    this->reset      = 1;
    this->latch      = 0;
}

void SevenSegments::SwitchOn()
{
    this->brightness = 1;
    Send();
    tr_debug("Switch on");
}

void SevenSegments::SwitchOff()
{
    this->brightness = 0;
    Send();
    tr_debug("Switch off");
}

void SevenSegments::Print(int number)
{
    tr_debug("Print integer %i", number);
    if (number < -9 || number > 99) {
        tr_warning("Number not available");
        PrintError(true);
        return;
    }

    if (number < 0) {
        tr_debug("Negative number");
        PrintDigit(-number);
        this->display.write(kDash);
    } else {
        for (int i = 0; i < 2; i++) {
            int digit = number % 10;
            number /= 10;
            PrintDigit(digit);
        }
    }

    Send();
}

void SevenSegments::Print(double number)
{
    tr_debug("Print double %f", number);
    if (number <= -9.5 || number >= 99.5) {
        tr_warning("Number not available");
        PrintError(false);
        return;
    }

    int unit = number;
    bool dot = (number - unit) != 0;
    if (unit < 0) {
        tr_debug("Negative number");
        PrintDigit(round(-number), (number - unit) != 0);
        this->display.write(kDash);
    } else if (unit < 10) {
        unit      = round(number * 10);
        int digit = unit % 10;
        unit /= 10;
        PrintDigit(digit, false);
        digit = unit % 10;
        PrintDigit(digit, true);
    } else {
        unit      = round(number);
        int digit = unit % 10;
        unit /= 10;
        PrintDigit(digit, dot);
        digit = unit % 10;
        PrintDigit(digit, false);
    }

    Send();
}

void SevenSegments::PrintError(bool integer = true)
{
    tr_debug("Print error");
    this->display.write(kDash);
    if (integer) {
        this->display.write(kDash);
    } else {
        this->display.write(kDashDotted);
    }
    Send();
}

void SevenSegments::PrintDigit(int digit, bool dot)
{
    tr_debug("Print digit %i, dotted: %i", digit, dot);
    if (digit == 10) {
        this->display.write(0);
    } else {
        int element = kPatterns[digit] << 1 | dot;
        tr_debug("%i", element);
        this->display.write(element);
    }
}

void SevenSegments::Send()
{
    this->latch = 1;
    this->latch = 0;
    tr_debug("Send");
}

void SevenSegments::operator=(int number) { Print(number); }

void SevenSegments::operator=(double number) { Print(number); }