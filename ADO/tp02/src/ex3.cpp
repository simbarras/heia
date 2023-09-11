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
 * @file ex3.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Find the number of instruction to run 1 sec
 *
 * @date 2021-10-17
 * @version 0.1.0
 ***************************************************************************/

#include <DiscoConsole.h>
#include <DiscoAssert.hpp>
#include <libopencm3/cm3/assert.h>
#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <stdint.h>
#include <stdio.h>

#include <DiscoAssert.hpp>
#include <Systick.hpp>

constexpr auto kPollingFrequency         = 1000;  // in Hz
constexpr uint64_t kNbInstructionDefault = 500;
constexpr auto kOneSec                   = 1000;
static volatile uint32_t system_millis   = 0;  // current time

static void setup(void)
{
    rcc_periph_clock_enable(RCC_TIM2);
    rcc_periph_reset_pulse(RST_TIM2);

    // timer configuration
    timer_set_period(TIM2, rcc_apb2_frequency / kPollingFrequency);
    timer_enable_irq(TIM2, TIM_DIER_UIE);
    timer_enable_counter(TIM2);

    nvic_enable_irq(NVIC_TIM2_IRQ);
}

void tim2_isr(void)
{
    timer_clear_flag(TIM2, TIM_SR_UIF);  // acknowledge interrupt
    system_millis++;
}

int main()
{
    DiscoConsoleSetup();
    SystickSetup();
    setup();
    tim2_isr();

    auto time          = 0;
    auto nbIstructions = kNbInstructionDefault;

    printf("start\n");

    while (time != kOneSec) {
        auto timeStart = system_millis;
        cm3_assert(nbIstructions >= 0);
        for (uint64_t i = 0; i < nbIstructions; i++) {
            // Prevents the compiler's optimization pass from removing this while loop
            asm volatile("nop");
        }
        auto timeStop = system_millis;
        time          = timeStop - timeStart;
        if (time == 0) {
            time = 1;
        }
        cm3_assert(time > 0);
        float quotient = (float) kOneSec / (float) time;
        printf("%d millis with %lu instructions -> quotient %f\n", time, nbIstructions, quotient);
        nbIstructions = nbIstructions * quotient;
    }
    return 0;
}
