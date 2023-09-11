// Copyright 2021 Haute école d'ingénierie et d'architecture de Fribourg
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
 * @author SIMON BARRAS <simon.barras@edu.hefr.ch>, NICOLAS TERREAUX <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Main file of the project. This project will display animation with the rotary and show the
 * index.
 *
 * @date 2021-12-20
 * @version 0.1.0
 ***************************************************************************/

/*** INCLUDES **************************************************************/
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

#include "Buttons.hpp"
#include "Rotary.hpp"

/*** CONSTANTS *************************************************************/
constexpr uint8_t kMaxNumber = 99;
constexpr uint8_t kMinNumber = 0;

/*** GLOBAL VARIABLES ******************************************************/
Counter counter(kMinNumber, kMinNumber, kMaxNumber);

/*** FUNCTIONS *************************************************************/
/**
 * @brief Main loop of the program. Initialize all the components and update vue.
 */
int main()
{
    DiscoConsoleSetup();
    SystickSetup();
    DiscoLcdSetup();

    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    auto backLight = PWM(PWM::PF5);
    backLight.SetDutyCycle(40);

    cm3_assert(DiscoLcdId() == kLcdST7789H2Id);
    auto gfx = DiscoLcdGFX(kLcdScreenWidth, kLcdScreenHeight);
    gfx.setFont(&IBMPlexMono_Bold60pt7b);
    gfx.setTextColor(0xFFFF);

    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);

    Joystick& joystick = Joystick::getInstance();

    joystick.RegisterHandler(Joystick::Right, new ButtonRight(counter));
    joystick.RegisterHandler(Joystick::Left, new ButtonLeft(counter));
    joystick.RegisterHandler(Joystick::Up, new ButtonUp());
    joystick.RegisterHandler(Joystick::Down, new ButtonDown());
    joystick.RegisterHandler(Joystick::Select, new ButtonSelect(counter));
    auto seg = Disco7Segments(0);
    seg.SwitchOn();
    seg.Print(15);
    Rotary rotary(counter, 2000, 2000);
    rotary.ShowLedPattern(1 << 15);

    Poller::getInstance().AddTicker(&rotary);
    Poller::getInstance().AddTicker(&joystick);

    char buffer[8];
    int oldCounter    = -1;
    int oldBrightness = -1;
    while (1) {
        if (oldBrightness != brightess) {
            seg.SetBrightness(brightess);
            oldBrightness = brightess;
        }

        int c = counter.GetValue();
        if (c != oldCounter) {
            seg.Print(counter.GetValue());
            rotary.UpdateLEDPattern();
            sprintf(buffer, "%02d", c);
            int16_t x, y;
            uint16_t w, h;
            gfx.getTextBounds(buffer, 40, 150, &x, &y, &w, &h);
            gfx.fillRect(x - 5, y - 5, w + 10, h + 10, 0x0000);
            gfx.setCursor(40, 150);
            gfx.write(buffer);
            oldCounter = c;
        }

        asm volatile("nop");
    }
}
