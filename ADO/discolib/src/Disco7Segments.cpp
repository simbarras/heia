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
 * @author Simon Barras <simon.barras@edu.hefr.ch>, Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Implementation of the 7 segments.
 *
 * @date 2021-11-02
 * @version 0.0.1
 ***************************************************************************/
#include "Disco7Segments.hpp"

#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>

#include <PWM.hpp>

#include "ClickShiftRegister.hpp"

const uint8_t kPatterns[] = {
    0b01111110,  // 0
    0b01010000,  // 1
    0b01101101,  // 2
    0b01111001,  // 3
    0b01010011,  // 4
    0b00111011,  // 5
    0b00111111,  // 6
    0b01110000,  // 7
    0b01111111,  // 8
    0b01111011,  // 9
    0b01110111,  // A
    0b00011111,  // b
    0b00101110,  // C
    0b01011101,  // d
    0b00101111,  // E
    0b00100111,  // F
                 // 0b point top-right top bottom-right bottom bottom-left top-left mid
};

// ----- Constructor -----

Disco7Segments::Disco7Segments(int id)
    : register_(id), pwm_(id == 0 ? PWM::Pin::PF3 : PWM::Pin::PF10, 10000, 100)
{
    SetBrightness(100);
}

// ----- Public methods -----

void Disco7Segments::PrintPattern(uint16_t pattern) { register_.SendShort(pattern); }

uint16_t Disco7Segments::GetPattern(int i, int base)
{
    uint16_t pattern = 0;
    for (auto k = 0; k < 2; k++) {
        pattern |= kPatterns[i % base] << 8 * k;
        i /= base;
    }
    return pattern;
}

void Disco7Segments::Print(int i) { PrintPattern(GetPattern(i, 10)); }

void Disco7Segments::PrintHex(int i) { PrintPattern(GetPattern(i, 16)); }

void Disco7Segments::SwitchOn() { SetBrightness(100); }

void Disco7Segments::SwitchOff() { SetBrightness(0); }

void Disco7Segments::SetBrightness(int brightness) { pwm_.SetDutyCycle(brightness); }