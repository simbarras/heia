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
 * @file button_isr.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Interface of the button
 *
 * @date 2022-03-09 to 2020-03-09
 * @version 0.1.0
 ***************************************************************************/

#ifndef BUTTON_ISR_HPP_
#define BUTTON_ISR_HPP_

#include <mbed.h>

#include "sensor_data_server.hpp"

class ButtonISR {
   public:
    ButtonISR(PinName pin, SensorDataServer* sensor);
    void fall_handler();
    void start_handling();

   private:
    void fall_handler_deferred();
    void dispatcher();

    InterruptIn button_;
    SensorDataServer* sensor_;
    EventQueue e_queue_;
    Thread thread_;
    DigitalOut control_led_;
};

#endif /* BUTTON_ISR_HPP_ */
