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
 * @date 2022-05-17 to 2022-05-17
 * @version 0.2.0
 ***************************************************************************/
#include "esp32.hpp"
#include "mbed.h"
#include "mbed_trace.h"

#if defined(MBED_CONF_MBED_TRACE_ENABLE)
#define TRACE_GROUP "Main"
#endif  // MBED_CONF_MBED_TRACE_ENABLE

static constexpr int kLedOn     = 0;
static constexpr int kLedOff    = 1;
static constexpr int kLeftSide  = 1;
static constexpr int kRightSide = 2;

// BME sensor(A4, A5, 0xEC);  // sda, clk, 8bit address (NUCLEO-L432)
DigitalOut led(LED4, kLedOff);

void getIPInfo(Esp32* esp_)
{
    uint8_t ip[4] = {0, 0, 0, 0}, gw[4] = {0, 0, 0, 0}, msk[4] = {0, 0, 0, 0};
    if (esp_->getIPAddress(ip, gw, msk)) {
        printf("Wi-Fi IP Address: %d.%d.%d.%d\n", ip[0], ip[1], ip[2], ip[3]);
        printf("Wi-Fi Gateway: %d.%d.%d.%d\n", gw[0], gw[1], gw[2], gw[3]);
        printf("Wi-Fi IP Mask: %d.%d.%d.%d\n", msk[0], msk[1], msk[2], msk[3]);
    } else {
        printf("No IP Adresse info !!\n");
    }
}

int main()
{
    // Initialize the log module
#if defined(MBED_CONF_MBED_TRACE_ENABLE)
    mbed_trace_init();
#endif

    // Program
    tr_info("Strart program");

    // WIFI
    Esp32 esp32_(ClickBoard::Id::LEFT);
    ThisThread::sleep_for(1s);

    tr_info("Connect to WIFI");
    bool isConnected = esp32_.wifiConnect("Lab-C0016", "Micro-Lab");
    if (!isConnected) {
        tr_error("WIFI connection failed");
        return -1;
    }

    for (int i = 0; i < 10; i++) {
        asm volatile("nop");
        led = !led;  // Blink led to see the statusge
        getIPInfo(&esp32_);
        ThisThread::sleep_for(1s);
    }

    tr_info("Disconnect WIFI");
    esp32_.wifiDisconnect();

    return 0;

    /*
AT+CIPSNTPCFG: Query/Set the time zone and SNTP server.

AT+CIPSNTPTIME: Query the SNTP time.
    */
}