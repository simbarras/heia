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
 * @file Buttons.hpp
 * @author SIMON BARRAS <simon.barras@edu.hefr.ch>
 * @author Terreaux Nicolas <nicolas.terreaux@edu.hefr.ch>
 *
 * @brief Contains all implementations for the 5 buttons
 *
 * @date 2021-01-11
 * @version 0.1.0
 ***************************************************************************/

/*** INCLUDES **************************************************************/
#include "ButtonHandler.hpp"
#include "tower.h"

/*** CONSTANTS *************************************************************/
const int MAX_LEVEL         = 16;
const int MIN_LEVEL         = 3;
const int MAX_VALUE         = 99;
const int MIN_VALUE         = 0;
const int LONG_PRESSED_TIME = 1000;
const int REPEAT_TIME       = 50;

/*** VARIABLES *************************************************************/
int level = 0;

class ButtonRight : public ButtonHandler {
   public:
    ButtonRight() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override{};
    void OnReleased() override{};
    void OnRepeated(int repeated) { (void)repeated; };

   private:
    int state_;
};
class ButtonLeft : public ButtonHandler {
   public:
    ButtonLeft() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override{};
    void OnReleased() override{};
    void OnRepeated(int repeated) { (void)repeated; };

   private:
    int state_;
};

/**
 * @brief Class that defines the behavior of the up button
 *
 */
class ButtonUp : public ButtonHandler {
   public:
    ButtonUp() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override
    {
        printf("Pressed\n");
        if (state_ == 0 && level != MAX_LEVEL) level += 1;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };
    void OnRepeated(int repeated)
    {
        (void)repeated;
        // if (level < MAX_LEVEL) level += 1;
    };

    int state_;
};

/**
 * @brief Class that defines the behavior of the down button
 *
 */
class ButtonDown : public ButtonHandler {
   public:
    ButtonDown() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override
    {
        printf("Pressed\n");
        if (state_ == 0 && level != MIN_LEVEL) level -= 1;
        state_ = 1;
    };
    void OnReleased() override { state_ = 0; };
    void OnRepeated(int repeated)
    {
        (void)repeated;
        // if (level > MIN_LEVEL) level -= 1;
    };
    int state_;
};

class ButtonSelect : public ButtonHandler {
   public:
    ButtonSelect() : ButtonHandler(LONG_PRESSED_TIME, REPEAT_TIME), state_{0} {}
    void OnPressed() override{};
    void OnReleased() override{};
    void OnRepeated(int repeated) { (void)repeated; };

   private:
    int state_;
};