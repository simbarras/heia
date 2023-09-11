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
 * @file ShiftRegister.cpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Send bits to the shift register
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#include "ShiftRegister.hpp"

#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <stdint.h>

ShiftRegister::ShiftRegister(int id)
{
    // Ports
    mrPort_  = GPIOC;
    sckPort_ = GPIOA;
    sdoPort_ = GPIOA;
    sdiPort_ = GPIOA;
    sckPin_  = GPIO5;
    sdoPin_  = GPIO6;
    sdiPin_  = GPIO7;

    // Check if right or left part of the compound, then assign corresponding value
    if (id == 0) {
        latchPort_ = GPIOA;
        mrPin_     = GPIO4;
        latchPin_  = GPIO15;

    } else if (id == 1) {
        latchPort_ = GPIOB;
        mrPin_     = GPIO3;
        latchPin_  = GPIO8;
        rcc_periph_clock_enable(RCC_GPIOB);
    }

    rcc_periph_clock_enable(RCC_GPIOA);
    rcc_periph_clock_enable(RCC_GPIOC);

    // configure GPIOs
    gpio_set(mrPort_, mrPin_);
    gpio_mode_setup(mrPort_, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, mrPin_);

    gpio_clear(sckPort_, sckPin_);
    gpio_mode_setup(sckPort_, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, sckPin_);

    gpio_clear(sdoPort_, sdoPin_);
    gpio_mode_setup(sdoPort_, GPIO_MODE_INPUT, GPIO_PUPD_NONE, sdoPin_);

    gpio_clear(sdiPort_, sdiPin_);
    gpio_mode_setup(sdiPort_, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, sdiPin_);

    gpio_clear(latchPort_, latchPin_);
    gpio_mode_setup(latchPort_, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, latchPin_);
}

void ShiftRegister::Latch()
{
    gpio_set(latchPort_, latchPin_);
    gpio_clear(latchPort_, latchPin_);
}

void ShiftRegister::SendBit(int bit)
{
    if (bit != 0) {
        gpio_set(sdiPort_, sdiPin_);
    } else {
        gpio_clear(sdiPort_, sdiPin_);
    }
    gpio_set(sckPort_, sckPin_);
    gpio_clear(sckPort_, sckPin_);
    gpio_clear(sdiPort_, sdiPin_);
}

// ----- Public methods -----

void ShiftRegister::Reset()
{
    gpio_clear(resetPort_, resetPin_);
    gpio_set(resetPort_, resetPin_);
}

void ShiftRegister::SendByte(uint8_t data)
{
    for (int i = 0; i < 8; i++) {
        SendBit((data & (1 << i)));
    }
    Latch();
}

void ShiftRegister::SendShort(uint16_t data)
{
    for (int i = 0; i < 16; i++) {
        SendBit((data & (1 << i)));
    }
    Latch();
}
