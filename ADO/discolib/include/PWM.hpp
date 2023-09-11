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
 * @brief Defines the interface of the PWM.
 *
 * @date 2021-11-16
 * @version 0.0.1
 ***************************************************************************/
#ifndef PWM_HPP_
#define PWM_HPP_
class PWM {
   public:
    // clang-format off
    enum Pin {
        PA0, PA1, PA2, PA3, PA6, PA7,
        PB0, PB1, PB4, PB5, PB6, PB7, PB8, PB9,
        PC6, PC7, PC8, PC9,
        PD12, PD13, PD14, PD15,
        PF3, PF4, PF5, PF10
    };
    // clang-format on

    PWM(Pin pin, int frequency = 25000, int dutyCycle = 10);
    void SetDutyCycle(int dutyCycle);
    void SetFrequency(int frequency);

    int DutyCycle() const;
    int Frequency() const;
    int Period() const;

   private:
    Pin pin_;
    int frequency_;
    int dutyCycle_;
};
#endif