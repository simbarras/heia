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
 * @file sensor_data_server.hpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Api to read sensor data
 *
 * @date 2022-03-16 to 2020-03-16
 * @version 0.1.0
 ***************************************************************************/
#ifndef SENSOR_DATA_SERVER_HPP_
#define SENSOR_DATA_SERVER_HPP_

#include "BME.h"
#include "Mail.h"
#include "mbed_mktime.h"

constexpr uint32_t queue_sz = 100;

typedef struct SensorData {
    float temperature;
    float humidity;
    float pressure;
    time_t time;
};
class SensorDataServer {
   public:
    SensorDataServer(Mail<SensorData, queue_sz>& mail, int slot);
    void start_acquisition();
    void stop_acquisition();
    void pollingData();

   private:
    void get_datas();
    float getTemperature();
    float getHumidity();
    float getPressure();

    Mail<SensorData, queue_sz>& mail_;
    BME weatherClick_;
    Thread thread_;
    bool is_running_;
    DigitalOut control_led_;
    Mutex mutex_;
};

#endif  // SENSOR_DATA_SERVER_HPP_