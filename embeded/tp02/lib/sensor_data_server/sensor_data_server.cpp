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
 * @file sensor_data_server.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Api to read sensor data
 *
 * @date 2022-03-16 to 2020-03-30
 * @version 0.1.0
 ***************************************************************************/

#include "sensor_data_server.hpp"

#include "mbed_trace.h"

static constexpr int kLedOn  = 0;
static constexpr int kLedOff = 1;

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "SensorDataServer"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

SensorDataServer::SensorDataServer(Mail<SensorData, queue_sz>& mail, int slot)
    : mail_(mail),
      weatherClick_(A4, A5, slot == 2 ? 0xEC : 0xEC),
      is_running_(true),
      control_led_(LED3, kLedOff)
{
    set_time(0);

    tr_debug("SensorDataServer created");
    if (!weatherClick_.init()) {
        tr_error("WeatherClick init failed");
        return;
    }
    tr_debug("WeatherClick initialized");
}

void SensorDataServer::start_acquisition()
{
    tr_info("Starting acquisition");
    thread_.start(callback(this, &SensorDataServer::get_datas));
}

void SensorDataServer::stop_acquisition()
{
    tr_info("Stopping acquisition");
    is_running_ = false;
    thread_.join();
}


void SensorDataServer::get_datas()
{
    while (is_running_) {
        SensorDataServer::pollingData();
        ThisThread::sleep_for(5s);
    }
}

// Retrieve data from the sensor
// During the execution of this function, the led is on
void SensorDataServer::pollingData()
{
    control_led_ = kLedOn;

    // Lock the critical section with a mutex
    mutex_.lock();
    SensorData* data  = mail_.alloc();
    data->temperature = SensorDataServer::getTemperature();
    data->humidity    = SensorDataServer::getHumidity();
    data->pressure    = SensorDataServer::getPressure();
    data->time        = time(NULL);
    mail_.put(data);

    // Unlock the critical section
    mutex_.unlock();

    control_led_ = kLedOff;
}

float SensorDataServer::getTemperature()
{
    tr_debug("getTemperature");
    return weatherClick_.getTemperature();
}

float SensorDataServer::getHumidity()
{
    tr_debug("getHumidity");
    return weatherClick_.getHumidity();
}

float SensorDataServer::getPressure()
{
    tr_debug("getPressure");
    return weatherClick_.getPressure();
}