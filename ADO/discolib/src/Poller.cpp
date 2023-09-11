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
 * @file Poller.cpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Implementation of the Poller
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

#include "Poller.hpp"

#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>

#include <Systick.hpp>

static const int kPollingFrequency = 2000;
Poller& instance_                  = Poller::getInstance();

Poller::Poller()
{
    // tickers_;
    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);

    // Initialize Timer
    rcc_periph_clock_enable(RCC_TIM2);
    rcc_periph_reset_pulse(RST_TIM2);

    timer_set_period(TIM2, rcc_apb2_frequency / kPollingFrequency);
    timer_enable_irq(TIM2, TIM_DIER_UIE);
    timer_enable_counter(TIM2);

    nvic_enable_irq(NVIC_TIM2_IRQ);
}

Poller& Poller::getInstance()
{
    static Poller* instance = new Poller;
    return *instance;
}

void Poller::AddTicker(Ticker* ticker) { tickers_.push_back(ticker); }

void Poller::Tick()
{
    for (auto& ticker : tickers_) {
        ticker->Tick();
    }
}

void Poller::SetFrequency(int frequency) { timer_set_period(TIM2, rcc_apb2_frequency / frequency); }

void tim2_isr(void)
{
    timer_clear_flag(TIM2, TIM_SR_UIF);  // acknowledge interrupt
    Poller::getInstance().Tick();
}