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
 * @file rotary.hpp
 * @author SIMON BARRAS <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Contains all implementations for the Rotary
 *
 * @date 2022-01-11
 * @version 0.1.0
 ***************************************************************************/

/*** INCLUDES **************************************************************/
#include <DiscoRotary.hpp>
#include <string>

#include "tower.h"

/*** CONSTANTS *************************************************************/
const int kNbEnabledLeds = 0b0000000000000111;

/*** ENUMS *****************************************************************/
enum LedState { LED_LEFT, LED_MIDDLE, LED_RIGHT };

/**
 * @brief class that support the rotary encoder
 *
 */
class Rotary : public DiscoRotary {
   public:
    Rotary(int longPressedTime, int repeatTime) : DiscoRotary(longPressedTime, repeatTime) {}
    void OnRotate(int position)
    {
        printf("Rotate: %d\n", position);
        if (position == -1) {
            UpdateState(true);

        } else if (position == 1) {
            UpdateState(false);
        }
    }
    void OnPressed()
    {
        if (oldState == currentState) {
            printf("Rotary pressed same state\n");
        } else {
            printf("Rotary pressed at position: %d\n", currentState);
            int by = 3 - (oldState + currentState);
            int moves = tower_of_hanoi_move(oldState, currentState, by, level);
            display_moves(moves);

            oldState = currentState;
        }
    }
    void OnReleased()
    {
    }

    void UpdateLed(int shift) { ShowLedPattern(kNbEnabledLeds << shift); }

    void UpdateState(bool rotateLeft)
    {
        if (rotateLeft) {
            switch (currentState) {
                case LED_LEFT:
                    break;
                case LED_MIDDLE:

                    ShowLedPattern(7 << 10);
                    currentState = LED_LEFT;
                    break;
                case LED_RIGHT:
                    ShowLedPattern(7 << 6);
                    currentState = LED_MIDDLE;
                    break;
                default:
                    printf("ERROR");
                    break;
            }
        } else {
            switch (currentState) {
                case LED_LEFT:
                    ShowLedPattern(7 << 6);
                    currentState = LED_MIDDLE;
                    break;
                case LED_MIDDLE:
                    ShowLedPattern(7 << 2);
                    currentState = LED_RIGHT;
                    break;
                case LED_RIGHT:
                    break;
                default:
                    printf("ERROR");
                    break;
            }
        }
    }

    int GetCurrentState() { return currentState; }
    int GetOldState() { return oldState; }

   protected:
    LedState currentState = LED_LEFT;
    int oldState          = 0;
};
