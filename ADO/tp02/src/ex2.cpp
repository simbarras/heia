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

/**************************
 * @file ex2.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Turn on/off blue led with a press button and blinks green LED
 *
 * @date 2021-10-16
 * @version 0.1.0
 *************************/

#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <Systick.hpp>

constexpr auto kPollingFrequency = 100;  // in Hz
constexpr auto kRccBtnPort       = RCC_GPIOA;
constexpr auto kRccLedPort       = RCC_GPIOE;
constexpr auto kLedPort          = GPIOE;
constexpr auto kLedBluePin       = GPIO3;
constexpr auto kButtonPort       = GPIOA;
constexpr auto kButtonPin        = GPIO0;
constexpr auto kLedGreenPin      = GPIO0;
constexpr auto kSleepLoop        = 7000000;

static void setup(void)
{
    // activate the clock for the GPIOs
    rcc_periph_clock_enable(RCC_TIM2);
    rcc_periph_reset_pulse(RST_TIM2);

    // timer configuration
    timer_set_period(TIM2, rcc_apb2_frequency / kPollingFrequency);
    timer_enable_irq(TIM2, TIM_DIER_UIE);
    timer_enable_counter(TIM2);

    // controller interruption
    nvic_enable_irq(NVIC_TIM2_IRQ);

    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    rcc_periph_clock_enable(kRccBtnPort);
    rcc_periph_clock_enable(kRccLedPort);
    gpio_mode_setup(kLedPort, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, kLedGreenPin);
    gpio_mode_setup(kLedPort, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, kLedBluePin);
    gpio_mode_setup(kButtonPort, GPIO_MODE_INPUT, GPIO_PUPD_PULLDOWN, kButtonPin);
}

void tim2_isr(void)
{
    static int prevBtn = 0;
    timer_clear_flag(TIM2, TIM_SR_UIF);  // acknowledge interrupt

    uint16_t btn = gpio_get(kButtonPort, kButtonPin);
    if ((btn != prevBtn) && (btn != 0)) {
        gpio_toggle(kLedPort, kLedBluePin);
    }
    prevBtn = btn;
}

int main()
{
    setup();
    SystickSetup();

    while (1) {
        // Prevents the compiler's optimization pass from removing this while loop
        asm volatile("nop");

        // Blinks the green LED
        gpio_toggle(kLedPort, kLedGreenPin);
        SystickDelayMilliseconds(200);
    }
    return 0;
}