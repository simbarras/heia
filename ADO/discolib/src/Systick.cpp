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
 * @file Systick.cpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Implementation of systick.
 *
 * @date 2021-10-12
 * @version 0.1.0
 ***************************************************************************/
#include "Systick.hpp"

#include <libopencm3/cm3/nvic.h>
#include <libopencm3/cm3/systick.h>
#include <libopencm3/stm32/rcc.h>

#include <cstdint>

constexpr uint32_t kMillisecond = 1000;

static volatile uint32_t system_millis;

void sys_tick_handler(void) { system_millis++; }

void SystickSetup(void)
{
    systick_set_reload(rcc_ahb_frequency / kMillisecond);
    systick_set_clocksource(STK_CSR_CLKSOURCE_AHB);
    systick_counter_enable();
    systick_interrupt_enable();
}

/* sleep for delay milliseconds */
void SystickDelayMilliseconds(uint32_t delay)
{
    uint32_t now = system_millis;
    while (system_millis - now < delay)
        ;
}

uint32_t SystickSystemMillis(void) { return system_millis; }
