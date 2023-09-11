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
 * @file test.hpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Unit test
 *
 * @date 2022-01-11
 * @version 0.1.0
 ***************************************************************************/
#include <FontsGFX/FreeSans12pt7b.h>
#include <FontsGFX/IBMPlexMonoBold60pt7b.h>
#include <libopencm3/cm3/assert.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <unity.h>

#include <AdafruitGFX.hpp>
#include <DiscoAssert.hpp>
#include <DiscoLcd.hpp>
#include <PWM.hpp>

#include "tower.h"

constexpr uint8_t kMaxNumber = 99;
constexpr uint8_t kMinNumber = 0;
static void setup(void)
{
    // Enable clock
    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
}
void wait_2_sec()
{
    for (int i = 0; i < 54000000; i++) {
        asm volatile("nop");
    }
}

void test_hanoi(void)
{
    tower_of_hanoi_init(0, 3);
    wait_2_sec();
    int moves = tower_of_hanoi_move(0, 1, 2, 3);
    display_moves(moves);
    TEST_ASSERT_EQUAL(7, moves);
    TEST_MESSAGE("Peg 0 to 1 with 3 disks OK");

    wait_2_sec();
    tower_of_hanoi_init(1, 6);
    wait_2_sec();
    moves = tower_of_hanoi_move(1, 2, 0, 6);
    display_moves(moves);
    TEST_ASSERT_EQUAL(63, moves);
    TEST_MESSAGE("Peg 1 to 2 with 6 disks OK");

    wait_2_sec();
    tower_of_hanoi_init(2, 12);
    wait_2_sec();
    moves = tower_of_hanoi_move(2, 0, 1, 12);
    display_moves(moves);
    TEST_ASSERT_EQUAL(4095, moves);
    TEST_MESSAGE("Peg 2 to 0 with 12 disks OK");

    wait_2_sec();
    tower_of_hanoi_init(0, 10);
    wait_2_sec();
    moves = tower_of_hanoi_move(0, 1, 2, 10);
    display_moves(moves);
    TEST_ASSERT_EQUAL(1023, moves);
    TEST_MESSAGE("Peg 0 to 1 with 10 disks OK");
}

int main()
{
    setup();
    UNITY_BEGIN();
    wait_2_sec();
    RUN_TEST(test_hanoi);
    UNITY_END();
    return 0;
}