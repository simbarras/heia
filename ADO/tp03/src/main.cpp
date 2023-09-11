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
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Control the 7-segments with a joystick
 *
 * @date 2021-11-16
 * @version 0.1.0
 ***************************************************************************/
#include <Systick.hpp>

#include "Buttons.hpp"

int main()
{
    rcc_clock_setup_pll(&rcc_hse_8mhz_3v3[RCC_CLOCK_3V3_84MHZ]);
    Joystick::Setup();

    Joystick::RegisterHandler(Joystick::Right, new ButtonRight());
    Joystick::RegisterHandler(Joystick::Left, new ButtonLeft());
    Joystick::RegisterHandler(Joystick::Up, new ButtonUp());
    Joystick::RegisterHandler(Joystick::Down, new ButtonDown());
    Joystick::RegisterHandler(Joystick::Select, new ButtonSelect());
    auto seg = Disco7Segments();

    SystickSetup();
    DiscoConsoleSetup();

    while (1) {
        seg.SetBrightness(brightess);
        seg.Print(counter);
        asm volatile("nop");
    }
}
