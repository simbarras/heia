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
 * @file mqtt_publisher.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Module to publish data over MQTT
 *
 * @date 2022-04-27 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#ifndef MQTTPUBLISHER_HPP
#define MQTTPUBLISHER_HPP

#include "click_board.hpp"
#include "esp32.hpp"
#include "sensor_data_server.hpp"

char const* WIFI_SSID     = "Lab-C0016";
char const* WIFI_PASSWORD = "Micro-Lab";
char const* MQTT_SERVER   = "io.adafruit.com";
constexpr int MQTT_PORT   = 1883;
char const* MQTT_ID       = "";
char const* MQTT_USER     = "nterreaux";
char const* MQTT_PASSWORD = "Emf12345";

class MQTTPublisher {
   public:
    MQTTPublisher(ClickBoard::Id slot);
    ~MQTTPublisher();

    void publish(SensorData data);

   private:
    Esp32* esp32;
};

#endif