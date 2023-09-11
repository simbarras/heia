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
 * @file Joystick.cpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Implementation of the list of buttons
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#include "Joystick.hpp"

#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <string.h>

#include <Systick.hpp>

const struct {
    enum rcc_periph_clken rcc;
    uint32_t port;
    uint16_t pin;
} buttons_config[] = {
    [Joystick::Select] = {.rcc = RCC_GPIOA, .port = GPIOA, .pin = GPIO0},
    [Joystick::Down]   = {.rcc = RCC_GPIOG, .port = GPIOG, .pin = GPIO0},
    [Joystick::Left]   = {.rcc = RCC_GPIOF, .port = GPIOF, .pin = GPIO14},
    [Joystick::Right]  = {.rcc = RCC_GPIOF, .port = GPIOF, .pin = GPIO15},
    [Joystick::Up]     = {.rcc = RCC_GPIOG, .port = GPIOG, .pin = GPIO1},
};

constexpr int kNumberOfButtons = sizeof(buttons_config) / sizeof(buttons_config[0]);

Joystick::Joystick()
{
    handlers_.resize(kNumberOfButtons);
    for (auto btn : buttons_config) {
        rcc_periph_clock_enable(btn.rcc);
        gpio_mode_setup(btn.port, GPIO_MODE_INPUT, GPIO_PUPD_PULLDOWN, btn.pin);
    }

    for (int i = 0; i < kNumberOfButtons; i++) {
        handlers_[i] = nullptr;
    }
}

Joystick& Joystick::getInstance()
{
    static Joystick* instance = new Joystick;
    return *instance;
}

void Joystick::RegisterHandler(Button button, ButtonHandler* handler)
{
    handlers_[button] = handler;
}

void Joystick::Tick()
{
    uint32_t now = SystickSystemMillis();
    for (int i = 0; i < kNumberOfButtons; i++) {
        if (handlers_[i] != nullptr) {
            uint16_t value = gpio_get(buttons_config[i].port, buttons_config[i].pin);
            handlers_[i]->Tick(value != 0, now);
        }
    }
}
