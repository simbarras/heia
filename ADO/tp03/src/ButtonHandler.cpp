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
 * @file ButtonHandler.cpp
 * @author Barras Simon <simon.barras@edu.hefr.ch>, Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Implement the tick for all five buttons
 *
 * @date 2021-11-16
 * @version 0.1.0
 ***************************************************************************/
#include "ButtonHandler.hpp"

#include <DiscoConsole.h>
#include <stdint.h>
#include <stdio.h>

ButtonHandler::ButtonHandler(int longPressedTime, int repeatTime)
    : longPressedTime_(longPressedTime), repeatTime_(repeatTime)
{
}

void ButtonHandler::Tick(bool pressed, uint32_t ms)
{
    if (pressed == false) {
        OnReleased();
        timePassed_ = 0;
    }
    if (pressed) {
        OnPressed();

        if (timePassed_ == 0) timePassed_ = ms;
        // Pressed more than 1 second
        if (longPressedTime_ <= (ms - timePassed_)) {
            OnLongPressed();
        }
    }
}