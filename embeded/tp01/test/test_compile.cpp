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
 * @file test_compile.cpp
 * @author Jacques Supcik   <jacques.supcik@hefr.ch>
 * @author Simon Barras     <simon.barrask@edu.hefr.ch>
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Test the compilation of the template
 *
 * @date 2022-03.01
 * @version 0.1.0
 ***************************************************************************/

#include <string.h>
#include <unity.h>

#include <iostream>

#include "click_thermo.hpp"
#include "mbed.h"
#include "mbed_trace.h"
#include "seven_segments.hpp"
void thermo_test(void)
{
    ClickThermo cl;

    double currentTemp1 = cl.GetTemperature();

    printf("Temperature 1 =  %lf\n", currentTemp1);
    ThisThread::sleep_for(500ms);
    TEST_ASSERT_EQUAL(10 < currentTemp1, true);
    TEST_MESSAGE("Temperature is greater than 10 degrees");
    TEST_ASSERT_EQUAL(currentTemp1 < 35, true);
    TEST_MESSAGE("Temperature is lower than 10 degrees");
    ThisThread::sleep_for(5000ms);

    double currentTemp2 = cl.GetTemperature();
    printf("Temperature 2 =  %lf\n", currentTemp2);
    ThisThread::sleep_for(500ms);

    TEST_ASSERT_EQUAL(10 < currentTemp2, true);
    TEST_MESSAGE("Temperature 2 is greater than 10 degrees");
    TEST_ASSERT_EQUAL(currentTemp2 < 35, true);
    TEST_MESSAGE("Temperature 2 is lower than 10 degrees");
    ThisThread::sleep_for(500ms);

    double diffTemp = currentTemp1 - currentTemp2;
    printf("Difference =  %lf\n", diffTemp);
    ThisThread::sleep_for(500ms);

    TEST_ASSERT_EQUAL(diffTemp * diffTemp < 10, true);
    TEST_MESSAGE(
        "The temperature difference between the first and second measurement is less than 3 "
        "degrees");
}

void display_test(void)
{
    double kTests[] = {-9.5, -9.4, -4.8, 0, 0.39, 7.21, 12.3, 99.43, 99.5};

    SevenSegments sSegs(1);

    for (int i = 0; i < sizeof(kTests) / sizeof(kTests[0]); i++) {
        sSegs = kTests[i];
        printf("Test: %lf\n", kTests[i]);
        ThisThread::sleep_for(5000ms);
    }
    TEST_ASSERT_EQUAL(true, true);
}

int main()
{
    wait_us(2000000);
    UNITY_BEGIN();
    RUN_TEST(thermo_test);
    RUN_TEST(display_test);

    UNITY_END();
    while (1) {
        asm volatile("nop");
    }
}