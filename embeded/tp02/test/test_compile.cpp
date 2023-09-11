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
 * @file test_compile.cpp
 * @author Jacques Supcik   <jacques.supcik@hefr.ch>
 * @author Simon Barras     <simon.barrask@edu.hefr.ch>
 * @author Nicolas Terreaux <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Test the compilation of the template
 *
 * @date 2022-03-30
 * @version 0.1.0
 ***************************************************************************/

#include <string.h>
#include <unity.h>

#include "BME.h"
#include "Fonts/IBMPlexSansBold24pt8b.h"
#include "Fonts/IBMPlexSansMedium12pt8b.h"
#include "button_isr.hpp"
#include "disco_lcd_gfx.h"
#include "lcd_display.hpp"
#include "mbed.h"
#include "mbed_trace.h"
#include "sensor_data_server.hpp"

void thermo_test(void)
{
    // Intialize sensor and queue
    // Initialize the sensor and wait 1 second
    Mail<SensorData, queue_sz>* sensor_data_mailbox = new Mail<SensorData, queue_sz>();
    SensorDataServer sensor(*sensor_data_mailbox, 2);
    ThisThread::sleep_for(1000ms);

    // Launch the acquisition
    sensor.start_acquisition();
    osEvent event = sensor_data_mailbox->get();

    // catch the first mesurment
    // Get for the first time the data
    SensorData* data = (SensorData*)event.value.p;
    float tmp1       = data->temperature;
    float pres1      = data->pressure;
    float hum1       = data->humidity;

    sensor_data_mailbox->free(data);

    // Display the data and check if the data are plausible
    printf("Temperature 1 =  %lf\n", tmp1);
    printf("Pressure 1 =  %lf\n", pres1);
    printf("Humidity 1 =  %lf\n", hum1);
    ThisThread::sleep_for(500ms);
    TEST_ASSERT_EQUAL(10 < tmp1, true);
    TEST_ASSERT_EQUAL(900 < pres1, true);
    TEST_ASSERT_EQUAL(10 < hum1, true);
    TEST_MESSAGE(
        "Temperature is greater than 10 degrees, pressure is greater than 900mbar and humidity is "
        "greater than 10%");
    TEST_ASSERT_EQUAL(tmp1 < 35, true);
    TEST_ASSERT_EQUAL(pres1 < 1000, true);
    TEST_ASSERT_EQUAL(hum1 < 60, true);
    TEST_MESSAGE(
        "Temperature is lower than 10 degrees, pressure lower than 1000mbar and humidity lower "
        "than 60%");
    // catch the second mesurment
    ThisThread::sleep_for(5000ms);

    // Get for the second time the data
    float tmp2  = data->temperature;
    float pres2 = data->pressure;
    float hum2  = data->humidity;

    sensor_data_mailbox->free(data);

    // Display the second measurement

    // Show difference
    printf("Temperature 2 =  %lf\n", tmp2);
    printf("Pressure 2 =  %lf\n", pres2);
    printf("Humidity 2 =  %lf\n", hum2);
    ThisThread::sleep_for(500ms);

    // Check if the difference between the two measurements is plausible
    float diffTemp = tmp1 - tmp2;
    float diffPres = pres1 - pres2;

    // Test if the value are logical
    float diffHum = hum1 - hum2;
    printf("Difference temp =  %lf\n", diffTemp);
    printf("Difference pres =  %lf\n", diffPres);
    printf("Difference hum =  %lf\n", diffHum);
    ThisThread::sleep_for(500ms);

    TEST_ASSERT_EQUAL(diffTemp * diffTemp < 10, true);
    TEST_ASSERT_EQUAL(diffPres * diffPres < 10, true);
    TEST_ASSERT_EQUAL(diffHum * diffHum < 10, true);
    TEST_MESSAGE(
        "The difference between the first and second measurement of all arguments are less than 3 "
        "(degrees, mbar and %)");
}

void display_test(void)
{
    // Intialize the display
    LCDDisplay display;
    display.init();

    // Print plausible data
    display.printTemperature(20);
    display.printPressure(950);
    display.printHumidity(15);
    ThisThread::sleep_for(5000ms);

    // Print false data
    display.printTemperature(-50);
    display.printPressure(-800);
    display.printHumidity(-80);
    ThisThread::sleep_for(5000ms);

    // Print big data
    display.printTemperature(110);
    display.printPressure(2500);
    display.printHumidity(1500);
    ThisThread::sleep_for(5000ms);

    // Finish with plausible data
    display.printTemperature(18);
    display.printPressure(1010);
    display.printHumidity(45);
    ThisThread::sleep_for(5000ms);
}

#define MAX_THREAD_STACK 384
#define SAMPLE_TIME_MS 2000
#define LOOP_TIME_MS 3000

uint64_t prev_idle_time = 0;
int32_t wait_time_ms    = 5000;

void time_stat_test()
{
    mbed_stats_cpu_t stats;
    mbed_stats_cpu_get(&stats);

    // Calculate the percentage of CPU usage
    uint64_t diff_usec = (stats.idle_time - prev_idle_time);
    uint8_t idle       = (diff_usec * 100) / (SAMPLE_TIME_MS * 1000);
    uint8_t usage      = 100 - ((diff_usec * 100) / (SAMPLE_TIME_MS * 1000));
    prev_idle_time     = stats.idle_time;

    printf("\nTime(us): Up: %lld", stats.uptime);
    printf("   Idle: %lld", stats.idle_time);
    printf("   Sleep: %lld", stats.sleep_time);
    printf("   DeepSleep: %lld\n", stats.deep_sleep_time);
    printf("Idle: %d%% Usage: %d%%\n\n", idle, usage);
}

void test_no_consuming(){
    Mail<SensorData, queue_sz>* sensor_data_mailbox = new Mail<SensorData, queue_sz>();
    SensorDataServer sensor(*sensor_data_mailbox, 2);
    ThisThread::sleep_for(1s);

    sensor.start_acquisition();

    // Launch the acquisition
    sensor.start_acquisition();
    TEST_MESSAGE("Wait 30 seconds");
    ThisThread::sleep_for(30s);
    osEvent event = sensor_data_mailbox->get();

    // catch the first mesurment
    // Get for the first time the data
    SensorData* data = (SensorData*)event.value.p;
    TEST_MESSAGE("Test if the data are found");
    TEST_ASSERT_EQUAL(data != NULL, true);

}

int main()
{
    wait_us(2000000);
    UNITY_BEGIN();
    RUN_TEST(thermo_test);
    RUN_TEST(display_test);
    RUN_TEST(time_stat_test);
    RUN_TEST(test_no_consuming);
    UNITY_END();
    while (1) {
        asm volatile("nop");
    }
}