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
 * @file click_thermo.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras     <simon.barras@edu.hefr.ch>
 *
 * @brief Header file for the thermo
 *
 * @date 2022.02.13 to 2020.02.13
 * @version 0.0.1
 ***************************************************************************/
#ifndef CLICK_THERMO_H_
#define CLICK_THERMO_H_

#include "mbed.h"

class ClickThermo {
   public:
    ClickThermo();
    double GetTemperature();

   private:
    I2C i2c_;
};

#endif /*CLICK_THERMO_H_*/
