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
 * @file Rotary.hpp
 * @author SIMON BARRAS <simon.barras@edu.hefr.ch>, NICOLAS TERREAUX <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Contains all implementations for the Rotary
 *
 * @date 2021-11-30
 * @version 0.1.0
 ***************************************************************************/

/*** INCLUDES **************************************************************/
#include <DiscoRotary.hpp>

#include "Counter.hpp"

/*** ENUMS *****************************************************************/
enum LedState { LED_SINGLE, LED_DUAL, LED_QUAD, LED_FILL, LED_INVERTED };

/**
 * @brief class that support the rotary encoder
 *
 */
class Rotary : public DiscoRotary {
   public:
    Rotary(Counter& counter, int longPressedTime, int repeatTime)
        : DiscoRotary(longPressedTime, repeatTime), counter_(counter)
    {
    }
    void OnRotate(int position)
    {
        if (position == -1) {
            counter_.Decrement();
        } else if (position == 1) {
            counter_.Increment();
        }
    }
    void OnPressed()
    {
        printf("Rotary pressed");
        UpdateLEDMode();
    }
    void OnReleased() { UpdateLEDPattern(); }
    void OnLongPressed()
    {
        counter_.Reset();
        currentState = LED_SINGLE;
    }

    void UpdateLEDPattern()
    {
        switch (currentState) {
            case LED_SINGLE:
                ShowLedPattern(1 << (15 - counter_.GetValue() % 16));
                break;
            case LED_DUAL:
                ShowLedPattern(1 << (7 - counter_.GetValue() % 8) |
                               1 << (15 - counter_.GetValue() % 8));
                break;
            case LED_QUAD:
                ShowLedPattern(
                    1 << (3 - counter_.GetValue() % 4) | 1 << (7 - counter_.GetValue() % 4) |
                    1 << (11 - counter_.GetValue() % 4) | 1 << (15 - counter_.GetValue() % 4));
                break;
            case LED_INVERTED:
                ShowLedPattern(1 << counter_.GetValue() % 16);
                break;
            default:
                ShowLedPattern(0xffff << (16 - counter_.GetValue() / 6));
        }
    }

   protected:
    void UpdateLEDMode()
    {
        switch (currentState) {
            case LED_SINGLE:
                currentState = LED_DUAL;
                break;
            case LED_DUAL:
                currentState = LED_QUAD;
                break;
            case LED_QUAD:
                currentState = LED_FILL;
                break;
            case LED_FILL:
                currentState = LED_INVERTED;
                break;
            default:
                currentState = LED_SINGLE;
        }
    }

   protected:
    LedState currentState = LED_SINGLE;

   private:
    Counter& counter_;
};