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
 * @file DiscoAssert.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Redefinition of the assertion procedure
 *
 * @date 2021-10-12
 * @version 0.1.0
 ***************************************************************************/
#include "DiscoAssert.hpp"

#include <libopencm3/cm3/assert.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>

constexpr auto kRccLedPort = RCC_GPIOE;
constexpr auto kLedPort    = GPIOE;
constexpr auto kSleepLoop  = 3500000;

enum ledsColor {
    kLedGreen  = GPIO0,
    kLedOrange = GPIO1,
    kLedRed    = GPIO2,
    kLedBlue   = GPIO3,
};

// Set default blinking LEDs
constexpr uint32_t defaultLeds = kLedOrange | kLedRed;
static uint32_t blinkingLeds   = defaultLeds;

static void busyLoopDelay(void)
{
    for (int i = 0; i < kSleepLoop; i++) {
        asm volatile("nop");
    }
}

void discoAlertSetLeds(uint32_t leds)
{
    blinkingLeds = 0;
    blinkingLeds |= (leds & kDiscoAlertLedGreen)  ? kLedGreen  : 0;
    blinkingLeds |= (leds & kDiscoAlertLedOrange) ? kLedOrange : 0;
    blinkingLeds |= (leds & kDiscoAlertLedRed)    ? kLedRed    : 0;
    blinkingLeds |= (leds & kDiscoAlertLedBlue)   ? kLedBlue   : 0;
}

void cm3_assert_failed(void)
{
    // setup
    rcc_periph_clock_enable(kRccLedPort);
    gpio_set(kLedPort, kDiscoAlertAllLeds);  // switch off all LEDs
    gpio_mode_setup(
        kLedPort,
        GPIO_MODE_OUTPUT,
        GPIO_PUPD_NONE,
        kDiscoAlertAllLeds
    );

    // make sure that at least on LED will blink
    if (blinkingLeds == 0) {
        blinkingLeds = defaultLeds;
    }

    // do blink
    while (true) {
        gpio_toggle(kLedPort, blinkingLeds);
        busyLoopDelay();
    }
}