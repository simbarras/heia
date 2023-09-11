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
 * @file ex1.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Turn on/off blue led with a press button
 *
 * @date 2021-10-12
 * @version 0.1.0
 ***************************************************************************/

#include <DiscoConsole.h>
#include <libopencm3/cm3/assert.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <stdint.h>
#include <stdio.h>
#include <DiscoAssert.hpp>

constexpr auto kRccLedPort    = RCC_GPIOE;
constexpr auto kRccButtonPort = RCC_GPIOA;
constexpr auto kLedPort       = GPIOE;
constexpr auto kLedPin        = GPIO3;
constexpr auto kButtonPort    = GPIOA;
constexpr auto kButtonPin     = GPIO0;


static void setup(void)
{
    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    rcc_periph_clock_enable(kRccLedPort);
    rcc_periph_clock_enable(kRccButtonPort);
    gpio_mode_setup(kLedPort, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, kLedPin);
    gpio_mode_setup(kButtonPort, GPIO_MODE_INPUT, GPIO_PUPD_PULLDOWN, kButtonPin);
}

//Listen the button and when it's clicked, blue LED turn on/off
static void toggleOnClick()
{
    uint16_t oldRes = 0;
    uint16_t res    = 0;
    while (true) {
        res = gpio_get(kButtonPort, kButtonPin);
        if (res != 0) {
            //Check if has been "reset"
            if (res != oldRes) {
                //printf("Res: %i\t oldRes: %i\n", res, oldRes);
                gpio_toggle(kLedPort, kLedPin);
            }
        }
        oldRes = res;
    }
}

int main()
{
    setup();
    DiscoConsoleSetup();
    toggleOnClick();
    return 0;
}
