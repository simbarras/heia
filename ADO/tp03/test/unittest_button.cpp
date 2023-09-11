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
 * @file unittest_button.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Unit test for the button class. Don't work
 *
 * @date 2021-11-22
 * @version 0.1.0
 ***************************************************************************/
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <unity.h>

#include "ButtonHandler.hpp"
#include "Buttons.hpp"
#include "Joystick.hpp"
#include "Systick.hpp"
static void setup(void)
{
    // Enable clock
    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    SystickSetup();
}

void test_simple_button(void)
{
    counter = 0;
    // Disco7Segments seg = Disco7Segments();

    ButtonHandler* b = new ButtonRight();
    uint32_t timer   = 0;
    b->Tick(false, timer += 10);  // First tick is needed to initialize the button
    TEST_ASSERT_EQUAL(0, counter);
    TEST_PASS_MESSAGE("Right button OK");
}

int main()
{
    setup();
    UNITY_BEGIN();
    UNITY_BEGIN();
    RUN_TEST(test_simple_button);
    return 0;
}
