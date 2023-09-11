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
 * @file ble_publisher.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Module to publish data over Bluetooth Low Energy.
 *
 * @date 2022-04-27 to 2022-04-27
 * @version 0.1.0
 ***************************************************************************/

#include "ble_publisher.hpp"

#include "mbed_trace.h"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "BlePublisher"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

void BLEPublisher::init() {
    tr_debug("Initializing BLE publisher");
    esp32->startBlePublisher(true); }

void BLEPublisher::listen()
{
    while (true) {
        asm volatile("nop");
        osEvent event = sensor_data_mail->get();
        if (event.status == osEventMail) {
            SensorData* data = (SensorData*)event.value.p;

            // Print data
            tr_info("Temperature: %3.2f C", data->temperature, 0xb0);
            tr_info("Humidity:    %2.2f %%", data->humidity, 0x25);
            tr_info("Pressure:    %4.2f mbar's", data->pressure);
            tr_info("Time:        %s\n", ctime(&data->time));

            // Send data to BLE
            // convert int to hex
            char* dataHex     = new char[3 * 8];
            char* hexTemp     = (char*)&data->temperature;
            char* hexHumidity = (char*)&data->humidity;
            char* hexPressure = (char*)&data->pressure;
            for (size_t i = 0; i < 8; i++) {
                dataHex[i]      = hexTemp[i];
                dataHex[i + 8]  = hexHumidity[i];
                dataHex[i + 16] = hexPressure[i];
            }
            publish(dataHex, 3 * 8);
            delete dataHex;

            sensor_data_mail->free(data);
        }
    }
}

void BLEPublisher::publish(char* data, size_t size) { esp32->notifyPublisherData(data, size); }
