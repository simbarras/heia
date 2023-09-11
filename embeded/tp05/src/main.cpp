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
 * @author Barras Simon <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Main entry of the program. Check the state of the wheel and update the view selector
 *
 * @date 2022-01-11
 * @version 0.1.0
 ***************************************************************************/
#include <DiscoConsole.h>
#include <FontsGFX/FreeSans12pt7b.h>
#include <FontsGFX/IBMPlexMonoBold60pt7b.h>
#include <libopencm3/cm3/assert.h>
#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/timer.h>
#include <stdio.h>

#include <AdafruitGFX.hpp>
#include <ClickShiftRegister.hpp>
#include <Counter.hpp>
#include <Disco7Segments.hpp>
#include <DiscoAssert.hpp>
#include <DiscoLcd.hpp>
#include <Joystick.hpp>
#include <PWM.hpp>
#include <Poller.hpp>
#include <Systick.hpp>

#include "buttons.hpp"
#include "Rotary.hpp"
#include "canvas.hpp"
#include "color.h"
#include "tower.h"
#include "constants.h"

const uint8_t kPatterns[] = {
    0b00000110,  // left
    0b01010000,  // right
    0b00000000   // empty
    // 0b point top-right top bottom-right bottom bottom-left top-left mid
};

const int kLeftShift   = 10;
const int kMiddleShift = 6;
const int kRightShift  = 2;

int main()
{
    DiscoConsoleSetup();
    SystickSetup();
    DiscoLcdSetup();

    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    auto backLight = PWM(PWM::PF5);
    backLight.SetDutyCycle(40);

    cm3_assert(DiscoLcdId() == kLcdST7789H2Id);

    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    auto seg = Disco7Segments(0);
    seg.SwitchOn();
    Rotary rotary(2000, 2000);
    rotary.ShowLedPattern(7 << 10);
    tower_of_hanoi_init(0, NB_OF_DISKS_START);

    Joystick& joystick = Joystick::getInstance();
    joystick.RegisterHandler(Joystick::Right, new ButtonRight());
    joystick.RegisterHandler(Joystick::Left, new ButtonLeft());
    joystick.RegisterHandler(Joystick::Up, new ButtonUp());
    joystick.RegisterHandler(Joystick::Down, new ButtonDown());
    joystick.RegisterHandler(Joystick::Select, new ButtonSelect());

    Poller::getInstance().AddTicker(&rotary);
    Poller::getInstance().AddTicker(&joystick);

    auto oldState    = rotary.GetCurrentState();
    int led          = 0;
    uint16_t pattern = 0;
    seg.PrintPattern(kPatterns[0] << 8);

    int oldLevel = NB_OF_DISKS_START;
    level = oldLevel;
    while (1) {
        asm volatile("nop");
        auto currentState = rotary.GetCurrentState();
        if (oldLevel != level){
            printf("New level: %d\n", level);
            tower_of_hanoi_init(rotary.GetOldState(), level);
            oldLevel = level;
        }
        if (oldState != currentState) {
            printf("New state %d\n", currentState);
            switch (currentState) {
                case LED_LEFT:
                    led     = kLeftShift;
                    pattern = kPatterns[0] << 8;
                    break;
                case LED_MIDDLE:
                    led     = kMiddleShift;
                    pattern = (kPatterns[1] << 8) | kPatterns[0];
                    break;
                case LED_RIGHT:
                    led     = kRightShift;
                    pattern = kPatterns[1];
                    break;
            }
            rotary.UpdateLed(led);
            seg.PrintPattern(pattern);
            oldState = currentState;
        }

    }
}
