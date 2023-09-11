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
 * @file Disco7Segments.hpp
 * @author Jacques Supcik, Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux
 *<nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Defines the interface of the 7 segments.
 *
 * @date 2021-11-02
 * @version 0.0.1
 ***************************************************************************/
#ifndef DISCO7SEGMENTS_HPP_
#define DISCO7SEGMENTS_HPP_

#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <stdint.h>

#include <ClickShiftRegister.hpp>

#include "PWM.hpp"

class Disco7Segments {
   public:
    Disco7Segments(int id = 0);
    void Reset();
    void PrintPattern(uint16_t pattern);
    void Print(int i);
    void PrintHex(int i);

    void SwitchOn();
    void SwitchOff();
    void SetBrightness(int brightness);
    uint16_t GetPattern(int i, int base);

   private:
    ClickShiftRegister register_;
    PWM pwm_;
};

#endif /* DISCO7SEGMENTS_HPP_ */