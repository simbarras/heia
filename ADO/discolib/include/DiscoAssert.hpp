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
 * @file DiscoAssert.hpp
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Defines the interface of a method that allows us to choose the LED(s) we want to flash.
 *
 * @date 2021-10-12
 * @version 0.1.0
 ***************************************************************************/
#ifndef DISCOASSERT_HPP_
#define DISCOASSERT_HPP_

#include <libopencm3/stm32/gpio.h>

enum discoAlertLedsColor {
    kDiscoAlertLedGreen  = (1 << 0),
    kDiscoAlertLedOrange = (1 << 1),
    kDiscoAlertLedRed    = (1 << 2),
    kDiscoAlertLedBlue   = (1 << 3),
};

constexpr uint32_t kDiscoAlertAllLeds =
    kDiscoAlertLedGreen | kDiscoAlertLedOrange |
    kDiscoAlertLedRed   | kDiscoAlertLedBlue;

extern void discoAlertSetLeds(uint32_t leds);

#endif /* DISCOASSERT_HPP_ */