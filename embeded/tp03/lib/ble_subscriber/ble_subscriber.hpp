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
 * @file ble_subscriber.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Module to receive data over Bluetooth Low Energy.
 *
 * @date 2022-04-27 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#ifndef BLESUBSCRIBER_HPP
#define BLESUBSCRIBER_HPP

#include "click_board.hpp"
#include "esp32.hpp"
#include "sensor_data_server.hpp"

class BLESubscriber {
   public:
    BLESubscriber(ClickBoard::Id slot, Mail<SensorData, queue_sz>* mail)
        : esp32(new Esp32(slot)), sensor_data_mail(mail){
            esp32->setATDebug(true);
        };
    ~BLESubscriber();

    void init();
    void listen();

   private:
    Esp32* esp32;
    Mail<SensorData, queue_sz>* sensor_data_mail;
};

#endif