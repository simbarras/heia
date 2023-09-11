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
 * @file click_thermo.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief File that contains everything needed for the proper functioning of
 * the thermometer
 *
 * @date 2022.02.13 to 2020.02.13
 * @version 0.0.1
 ***************************************************************************/
#include "click_thermo.hpp"

#include "mbed.h"
#include "mbed_trace.h"

const int addr7bit  = 0x48;       // 7-bit I2C address
const int addr8bit  = 0x48 << 1;  // 8-bit I2C address, 0x90
const int frequency = 4000;
const auto SDA      = A4;
const auto SCL      = A5;

ClickThermo::ClickThermo() : i2c_(SDA, SCL) { i2c_.frequency(frequency); }

double ClickThermo::GetTemperature()
{
    char cmd[2];
    cmd[0] = 0x01;
    cmd[1] = 0x00;
    i2c_.write(addr8bit, cmd, 2);
    // read temperature register
    cmd[0] = 0x00;
    i2c_.write(addr8bit, cmd, 1);
    i2c_.read(addr8bit, cmd, 2);
    double tmp = (double((cmd[0] << 8) | cmd[1]) / 256.0);
    return tmp;
}