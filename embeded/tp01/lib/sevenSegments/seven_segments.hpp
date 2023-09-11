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
 * @file seven_segments.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Header file for a seven segment display that can display number.
 *
 * @date 2022.02.13 to 2020.02.13
 * @version 0.0.1
 ***************************************************************************/
#ifndef SEVEN_SEGMENT_H_
#define SEVEN_SEGMENT_H_

#include "mbed.h"

class SevenSegments {
   public:
    SevenSegments(int place);
    void SwitchOn();
    void SwitchOff();
    void Print(int number);
    void Print(double number);
    void operator=(int number);
    void operator=(double number);

   private:
    void PrintDigit(int, bool dot = false);
    void Send();
    void PrintError(bool integer);

    SPI display;
    DigitalOut brightness;
    DigitalOut reset;
    DigitalOut latch;
};

#endif /*SEVEN_SEGMENT_H_*/
