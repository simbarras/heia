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
 * @file main.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Entry point of the program. Simple hello world.
 *
 * @date 2022.02.13 to 2020.02.13
 * @version 0.0.1
 ***************************************************************************/

#include "click_thermo.hpp"
#include "mbed.h"
#include "mbed_trace.h"
#include "seven_segments.hpp"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "Main"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

static constexpr int kLedOn  = 0;
static constexpr int kLedOff = 1;

int main()
{
#if defined(MBED_CONF_MBED_TRACE_ENABLE)
    mbed_trace_init();
#endif
    // Create a SevenSegments object
    SevenSegments sSegs(1);
    ClickThermo thermo;
    DigitalOut led(LED4, kLedOff);
    sSegs.SwitchOn();

    while (true) {
        double tmp = thermo.GetTemperature();

        sSegs = tmp;
        printf("Temp = %lf\n", tmp);
        ThisThread::sleep_for(100ms);
    }
    return 0;
}
