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
 * @file DiscoRotary.cpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Implement the interface for the rotary
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#include "DiscoRotary.hpp"

#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <stdio.h>

#include <ButtonHandler.hpp>
#include <ClickShiftRegister.hpp>
#include <Systick.hpp>
DiscoRotary::DiscoRotary(int longPressedTime, int repeatTime, int id)
    : ButtonHandler(longPressedTime, repeatTime), register_(id), id_(id)
{
    // Ports

    encaPort_ = GPIOF;
    encbPin_  = GPIO1;

    // Check if right or left part of the compound, then assign corresponding value
    if (id_ == 0) {
        swPort_   = GPIOG;
        swPin_    = GPIO13;
        encaPin_  = GPIO3;
        encbPort_ = GPIOA;

        rcc_periph_clock_enable(RCC_GPIOG);

    } else if (id_ == 1) {
        swPort_   = GPIOF;
        swPin_    = GPIO4;
        encaPin_  = GPIO10;
        encbPort_ = GPIOC;

        rcc_periph_clock_enable(RCC_GPIOC);
    }

    rcc_periph_clock_enable(RCC_GPIOF);

    // configure GPIOs
    gpio_clear(swPort_, swPin_);
    gpio_mode_setup(swPort_, GPIO_MODE_OUTPUT, GPIO_PUPD_PULLDOWN, swPin_);

    gpio_clear(encaPort_, encaPin_);
    gpio_mode_setup(encaPort_, GPIO_MODE_OUTPUT, GPIO_PUPD_PULLDOWN, encaPin_);

    gpio_clear(encbPort_, encbPin_);
    gpio_mode_setup(encbPort_, GPIO_MODE_INPUT, GPIO_PUPD_PULLDOWN, encbPin_);
}
void DiscoRotary::ShowLedPattern(uint16_t pattern) { register_.SendShort(pattern); }

void DiscoRotary::Tick()
{
    uint32_t now   = SystickSystemMillis();
    uint16_t value = gpio_get(swPort_, swPin_) != 0;
    ButtonHandler::Tick(value, now);

    bool enca_out = gpio_get(encaPort_, encaPin_) != 0;
    bool encb_out = gpio_get(encbPort_, encbPin_) != 0;

    RotaryHandler::Tick(enca_out, encb_out);
}