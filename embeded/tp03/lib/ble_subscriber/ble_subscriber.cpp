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
 * @file ble_subscriber.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Module to revieve data over Bluetooth Low Energy.
 *
 * @date 2022-04-27 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#include "ble_subscriber.hpp"

#include "mbed_trace.h"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "BleSubscriber"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

void BLESubscriber::init()
{
    tr_debug("Initialize BLE subscriber");
    esp32->initBleSubscriber();
}

void BLESubscriber::listen()
{
    while (true) {
    }
}
