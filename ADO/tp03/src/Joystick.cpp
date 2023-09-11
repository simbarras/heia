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
 * @date 2021-11-16
 * @version 0.1.0
 ***************************************************************************/
#include "Joystick.hpp"

#include <DiscoConsole.h>
#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <stdio.h>

#include <Disco7Segments.hpp>

#include "Systick.hpp"

constexpr static struct {
    enum rcc_periph_clken rcc;
    uint32_t port;
    uint16_t pin;
} buttons_config[] = {[Joystick::Select] = {.rcc = RCC_GPIOA, .port = GPIOA, .pin = GPIO0},
                      [Joystick::Down]   = {.rcc = RCC_GPIOG, .port = GPIOG, .pin = GPIO0},
                      [Joystick::Left]   = {.rcc = RCC_GPIOF, .port = GPIOF, .pin = GPIO14},
                      [Joystick::Right]  = {.rcc = RCC_GPIOF, .port = GPIOF, .pin = GPIO15},
                      [Joystick::Up]     = {.rcc = RCC_GPIOG, .port = GPIOG, .pin = GPIO1}};

constexpr int kNumberOfButtons = sizeof(buttons_config) / sizeof(buttons_config[0]);

ButtonHandler* Joystick::handlers_[kNumberOfButtons]{};
Disco7Segments seg = Disco7Segments();
int currentNumber  = 0;

void Joystick::Setup()
{
    // Start clocks
    rcc_periph_clock_enable(RCC_GPIOA);
    rcc_periph_clock_enable(RCC_GPIOG);
    rcc_periph_clock_enable(RCC_GPIOF);

    // Configure the GPIOs for the 5 buttons and Reset handlers
    for (int i = 0; i < kNumberOfButtons; i++) {
        gpio_mode_setup(
            buttons_config[i].port, GPIO_MODE_INPUT, GPIO_PUPD_PULLDOWN, buttons_config[i].pin);

        Joystick::handlers_[i] = nullptr;
    }

    // Configure Timer2 for periodical polling (like TP02)
    rcc_periph_clock_enable(RCC_TIM2);
    rcc_periph_reset_pulse(RST_TIM2);
    timer_set_period(TIM2, rcc_apb2_frequency / kPollingFrequency_);
    timer_enable_irq(TIM2, TIM_DIER_UIE);
    timer_enable_counter(TIM2);
    nvic_enable_irq(NVIC_TIM2_IRQ);
}

void Joystick::Tick()
{
    uint32_t now = SystickSystemMillis();
    for (int i = 0; i < kNumberOfButtons; i++) {
        printf("Time %ld\n", now);
        if (handlers_[i] != nullptr) {
            uint16_t value = gpio_get(buttons_config[i].port, buttons_config[i].pin);
            handlers_[i]->Tick(value != 0, now);
        }
    }
}

void Joystick::RegisterHandler(Button button, ButtonHandler* handler)
{
    handlers_[button] = handler;
}

void tim2_isr(void)
{
    timer_clear_flag(TIM2, TIM_SR_UIF);  // acknowledge interrupt
    Joystick::Tick();
}