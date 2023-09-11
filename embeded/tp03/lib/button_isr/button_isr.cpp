// Copyright 2022 Haute école d'ingénierie et d'architecture de Fribourg
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
 * @file button_isr.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief File that contains the code for the button
 *
 * @date 2022-03-09 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#include <button_isr.hpp>

#include "mbed_trace.h"

static constexpr int kLedOn  = 0;
static constexpr int kLedOff = 1;

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "ButtonISR"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

ButtonISR::ButtonISR(PinName pin, SensorDataServer* sensor)
    : button_(pin), sensor_(sensor), control_led_(LED2, kLedOff)
{
    // Produc an interruption when the button is fall
    button_.fall(callback(this, &ButtonISR::fall_handler));
}

void ButtonISR::start_handling()
{
    // Launch the thread that look at the button
    thread_.start(callback(this, &ButtonISR::dispatcher));
}

void ButtonISR::dispatcher()
{
    // Put queue's thread in high priority
    osThreadSetPriority(osThreadGetId(), osPriorityHigh);
    e_queue_.dispatch_forever();
}

void ButtonISR::fall_handler_deferred(void)
{
    // this is executed in thread context
    tr_info("Button pressed");
    sensor_->pollingData();  // Fetch data when there is a event in the queue
    control_led_ = kLedOff;
}

void ButtonISR::fall_handler(void)
{
    // this is executed in ISR context
    control_led_ = kLedOn;
    // Add instruction to the queue
    e_queue_.call(callback(this, &ButtonISR::fall_handler_deferred));
}