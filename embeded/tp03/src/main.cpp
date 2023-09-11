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
 * @file main.cpp
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 * @author Simon Barras <simon.barras@edu.hefr.ch>
 *
 * @brief Main entry of the program
 *
 * @date 2022-03-09 to 2022-04-27
 * @version 0.2.0
 ***************************************************************************/
#include "BME.h"
#include "Fonts/IBMPlexSansBold24pt8b.h"
#include "Fonts/IBMPlexSansMedium12pt8b.h"
#include "ble_publisher.hpp"
#include "ble_subscriber.hpp"
#include "button_isr.hpp"
#include "click_board.hpp"
#include "disco_lcd_gfx.h"
#include "lcd_display.hpp"
#include "mbed.h"
#include "mbed_trace.h"
#include "sensor_data_server.hpp"
#include "mqtt_publisher.hpp"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "Main"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

static constexpr int kLedOn    = 0;
static constexpr int kLedOff   = 1;
static constexpr int leftSide  = 1;
static constexpr int rightSide = 2;

// BME sensor(A4, A5, 0xEC);  // sda, clk, 8bit address (NUCLEO-L432)
DigitalOut led(LED4, kLedOff);

float temp, humidity, pressure;

int main()
{
    // Initialize the log module
#if defined(MBED_CONF_MBED_TRACE_ENABLE)
    mbed_trace_init();
#endif

    // Intialize display
    LCDDisplay display;
    display.init();

#ifdef PUBLISHER
    // Program
    tr_info("Strart program as BLE publisher");

    // Initialize sensor with the mail for the communication
    Mail<SensorData, queue_sz>* sensor_data_mailbox = new Mail<SensorData, queue_sz>();
    SensorDataServer sensor(*sensor_data_mailbox, rightSide);

    // Initialize button
    ButtonISR button(PA_0, &sensor);
    button.start_handling();

    // Start collect data
    sensor.start_acquisition();

    // Initialize BLE
    BLEPublisher ble(ClickBoard::Id::LEFT, sensor_data_mailbox);
    ThisThread::sleep_for(1s);
    ble.init();
    // ble.listen();

    while (true) {
        asm volatile("nop");
        led = kLedOff;  // Blink led to see the status

        // Event when there is a data to display
        osEvent event = sensor_data_mailbox->get();
        if (event.status == osEventMail) {
            led              = kLedOn;
            SensorData* data = (SensorData*)event.value.p;

            // Print data
            display.printTemperature(data->temperature);
            tr_info("Temperature: %3.2f C", data->temperature, 0xb0);

            display.printHumidity(data->humidity);
            tr_info("Humidity:    %2.2f %%", data->humidity, 0x25);

            display.printPressure(data->pressure);
            tr_info("Pressure:    %4.2f mbar's", data->pressure);

            tr_info("Time:        %s\n", ctime(&data->time));

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
            // Send data to BLE
            ble.publish(dataHex, 3 * 8);
            delete dataHex;

            sensor_data_mailbox->free(data);  // Free memory
        }
    }
#endif
#ifdef SUBSCRIBER
    // Program
    tr_info("Strart program as BLE subscriber");

    // Initialize sensor with the mail for the communication
    Mail<SensorData, queue_sz>* data_mailbox = new Mail<SensorData, queue_sz>();

    // Initialize BLE
    BLESubscriber ble(ClickBoard::Id::LEFT, data_mailbox);

    ble.init();

    ThisThread::sleep_for(1s);
    // Send data to MQTT
    MQTTPublisher mqtt(ClickBoard::Id::LEFT);
    ThisThread::sleep_for(1s);

    SensorData data;
    data.temperature = 26.5;
    data.humidity = 33.9;
    data.pressure = 941;
    data.time = time(NULL);

    // ble.listen();

    while (true) {
        asm volatile("nop");
        led = !led;  // Blink led to see the status
        mqtt.publish(data);
        
        ThisThread::sleep_for(5s);
    }

#endif
}
