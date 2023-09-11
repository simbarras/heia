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
 * @file mqtt_publisher.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Module to publish data on adafruit.
 *
 * @date 2022-06-08 to 2022-06-08
 * @version 0.1.0
 ***************************************************************************/

#include "mqtt_publisher.hpp"

#include "mbed_trace.h"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "MQTTPublisher"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

MQTTPublisher::MQTTPublisher(ClickBoard::Id slot) : esp32(new Esp32(slot))
{
    tr_debug("Initializing MQTT publisher");
    esp32->wifiConnect(WIFI_SSID, WIFI_PASSWORD);
    // esp32->getIPAddress();
    if (esp32->mqttConnectBroker(MQTT_SERVER, MQTT_PORT, MQTT_ID, MQTT_USER, MQTT_PASSWORD)) {
        tr_debug("MQTT broker connected");
    } else {
        tr_error("MQTT broker connection failed");
    }
}

void MQTTPublisher::publish(SensorData data)
{
    tr_debug("Publishing data");
    auto temperature = to_string(data.temperature);
    esp32->mqttPublish("temperature", temperature.c_str(), temperature.length(), 0);
    auto humidity = to_string(data.humidity);
    esp32->mqttPublish("humidity", humidity.c_str(), humidity.length(), 0);
    auto pressure = to_string(data.pressure);
    esp32->mqttPublish("pressure", pressure.c_str(), pressure.length(), 0);
}

MQTTPublisher::~MQTTPublisher()
{
    tr_debug("Deinitializing MQTT publisher");
    esp32->wifiDisconnect();
    delete esp32;
}
